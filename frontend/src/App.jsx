import React, { useEffect, useMemo, useState } from 'react'
import { listApps, listEntities, listRules, createRule, evaluateRule, submitRule, approveRule, rejectRule } from './api.js'

export default function App(){
  const [apps, setApps] = useState([])
  const [appName, setAppName] = useState('App1')
  const [entities, setEntities] = useState([])
  const [rules, setRules] = useState([])

  const [name, setName] = useState('My First Rule')
  const [queryText, setQueryText] = useState('Department ids in (:deptSet) and employeeIds in (:empSet)')
  const [selectedEntities, setSelectedEntities] = useState(['Employee','Department'])
  const [paramValues, setParamValues] = useState({}) // parameter inputs keyed by param name

  const [jsonPreview, setJsonPreview] = useState('')
  const [evaluateResponse, setEvaluateResponse] = useState(null)

  useEffect(() => { listApps().then(setApps) }, [])
  useEffect(() => {
    if (!appName) return
    listEntities(appName).then(setEntities)
    listRules(appName).then(setRules)
  }, [appName])

  const entitiesOptions = entities.map(e=>e.name)

  function toggleEntity(eName){
    setSelectedEntities(prev => prev.includes(eName) ? prev.filter(x=>x!==eName) : [...prev, eName])
  }

  // Detect params like :deptSet, :empSet in the query
  const paramNames = useMemo(() => {
    const matches = queryText.match(/:\w+/g) || []
    const unique = Array.from(new Set(matches.map(m => m.slice(1))))
    return unique
  }, [queryText])

  // Initialize missing params in state when query changes and cleanup removed ones
  useEffect(() => {
    setParamValues(prev => {
      const next = { ...prev }
      paramNames.forEach(p => {
        if (next[p] === undefined) next[p] = ''
      })
      Object.keys(next).forEach(k => {
        if (!paramNames.includes(k)) delete next[k]
      })
      return next
    })
  }, [paramNames])

  function parseCsvToArray(txt){
    if (!txt || !txt.trim()) return []
    return txt
      .split(',')
      .map(s => s.trim())
      .filter(Boolean)
      .map(v => {
        const n = Number(v)
        return Number.isFinite(n) && String(n) === v ? n : v
      })
  }

  async function onCreate(){
    const variables = paramNames.reduce((acc, p) => {
      acc[p] = parseCsvToArray(paramValues[p] || '')
      return acc
    }, {})
    const payload = { appName, name, queryText, entities: selectedEntities, variables }
    const res = await createRule(payload)
    setRules(r=>[res, ...r])
    setJsonPreview(res.compiledJson)
  }

  async function onSubmit(id){ const res = await submitRule(id); setRules(rs=>rs.map(r=>r.id===id?res:r)) }
  async function onApprove(id){ const res = await approveRule(id); setRules(rs=>rs.map(r=>r.id===id?res:r)) }
  async function onReject(id){ const res = await rejectRule(id); setRules(rs=>rs.map(r=>r.id===id?res:r)) }

  async function onEvaluate(){
    // Prefer user-provided variables if present; otherwise fallback to a tiny dummy
    const userVariables = paramNames.length
      ? paramNames.reduce((acc, p) => {
          const arr = parseCsvToArray(paramValues[p] || '')
          if (arr.length) acc[p] = arr
          return acc
        }, {})
      : {}

    const fallback = { deptSet:[10,11], empSet:[1001,1002] }
    const variables = Object.keys(userVariables).length ? userVariables : fallback

    const record = { departmentId:10, employeeId:1002 }
    const anyRule = rules[0]
    if (!anyRule) return
    const res = await evaluateRule({ ruleId:anyRule.id, variables, record })
    setEvaluateResponse(res)
  }

  const summary = useMemo(()=>{
    return `If ${queryText}`
  }, [queryText])

  return (
    <div className="container grid" style={{gap:24}}>
      <header className="row" style={{justifyContent:'space-between'}}>
        <h1>Rule Dashboard</h1>
        <div className="row">
          <label>Application</label>
          <select value={appName} onChange={e=>setAppName(e.target.value)}>
            {apps.map(a=> <option key={a.id} value={a.name}>{a.name}</option>)}
          </select>
        </div>
      </header>

      <section className="grid" style={{gridTemplateColumns:'1fr 1fr'}}>
        <div className="card">
          <h2>Create Rule</h2>
          <div className="row">
            <input value={name} onChange={e=>setName(e.target.value)} placeholder="Rule name" />
          </div>
          <div style={{marginTop:8}}>
            <small className="muted">Entities tagged to {appName}:</small>
            <div className="row" style={{marginTop:8}}>
              {entitiesOptions.map(en => (
                <label key={en} className="badge">
                  <input type="checkbox" checked={selectedEntities.includes(en)} onChange={()=>toggleEntity(en)} /> {en}
                </label>
              ))}
            </div>
          </div>
          <div style={{marginTop:8}}>
            <textarea rows={4} value={queryText} onChange={e=>setQueryText(e.target.value)} placeholder="Type your condition, e.g. Department ids in (:deptSet) and employeeIds in (:empSet)"></textarea>
          </div>

          {paramNames.length > 0 && (
            <div style={{marginTop:8}}>
              <small className="muted">Parameters detected from query</small>
              <div className="grid" style={{gap:8, marginTop:6}}>
                {paramNames.map(p => (
                  <div key={p} className="row" style={{alignItems:'center', gap:8}}>
                    <label style={{minWidth:120}}>{p}</label>
                    <input
                      placeholder="Comma-separated values, e.g. 10,11 or A,B"
                      value={paramValues[p] ?? ''}
                      onChange={e=>setParamValues(v=>({ ...v, [p]: e.target.value }))}
                      style={{flex:1}}
                    />
                  </div>
                ))}
              </div>
              <small className="muted">Tip: numbers are auto-detected; others are kept as strings.</small>
            </div>
          )}

          <div className="row" style={{marginTop:8}}>
            <button className="primary" onClick={onCreate}>Create</button>
          </div>
          <div style={{marginTop:8}}>
            <small className="muted">Summary</small>
            <pre>{summary}</pre>
            <small className="muted">Compiled JSON</small>
            <pre>{jsonPreview || 'Create to see compiled JSON'}</pre>
          </div>
        </div>

        <div className="card">
          <h2>Evaluate (Dummy)</h2>
          <p className="muted">Evaluates the newest rule with parameters entered above (or a fallback).</p>
          <button onClick={onEvaluate}>Run Evaluation</button>
          {evaluateResponse && (
            <div style={{marginTop:12}}>
              <strong>Match:</strong> {String(evaluateResponse.match)}
            </div>
          )}
        </div>
      </section>

      <section className="card">
        <h2>Rules</h2>
        <table className="table">
          <thead>
            <tr><th>ID</th><th>Name</th><th>Status</th><th>Actions</th></tr>
          </thead>
          <tbody>
            {rules.map(r => (
              <tr key={r.id}>
                <td>#{r.id}</td>
                <td>{r.name}</td>
                <td><span className="badge">{r.status}</span></td>
                <td className="row">
                  <button onClick={()=>onSubmit(r.id)}>Submit</button>
                  <button onClick={()=>onApprove(r.id)}>Approve</button>
                  <button onClick={()=>onReject(r.id)}>Reject</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </section>
    </div>
  )
}
