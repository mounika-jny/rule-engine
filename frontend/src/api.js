const API = (path) => `http://localhost:8080${path}`;

export async function listApps(){ return fetch(API('/api/apps')).then(r=>r.json()); }
export async function listEntities(app){ return fetch(API(`/api/apps/${encodeURIComponent(app)}/entities`)).then(r=>r.json()); }
export async function listRules(app){ const q=app?`?app=${encodeURIComponent(app)}`:''; return fetch(API(`/api/rules${q}`)).then(r=>r.json()); }
export async function createRule(payload){ return fetch(API('/api/rules'), {method:'POST', headers:{'Content-Type':'application/json'}, body:JSON.stringify(payload)}).then(r=>r.json()); }
export async function evaluateRule(payload){ return fetch(API('/api/rules/evaluate'), {method:'POST', headers:{'Content-Type':'application/json'}, body:JSON.stringify(payload)}).then(r=>r.json()); }
export async function submitRule(id){ return fetch(API(`/api/workflow/${id}/submit`), {method:'POST'}).then(r=>r.json()); }
export async function approveRule(id){ return fetch(API(`/api/workflow/${id}/approve`), {method:'POST'}).then(r=>r.json()); }
export async function rejectRule(id){ return fetch(API(`/api/workflow/${id}/reject`), {method:'POST'}).then(r=>r.json()); }
