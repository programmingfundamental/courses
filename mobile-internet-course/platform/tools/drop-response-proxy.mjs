import http from 'node:http';
let dropped = false;
const upstream = process.env.LAB_UPSTREAM ?? 'http://127.0.0.1:8080';
if (!/^http:\/\/(127\.0\.0\.1|localhost):\d+$/.test(upstream)) throw new Error('Only loopback upstream is allowed.');
const server = http.createServer(async (req,res) => {
  if (!req.url?.startsWith('/api/')) { res.writeHead(404).end(); return; }
  try {
    const parts=[]; let size=0;
    for await (const part of req) { size+=part.length; if(size>65536){res.writeHead(413).end();return;} parts.push(part); }
    const headers={};
    for (const name of ['content-type','authorization','idempotency-key','x-request-id']) if(req.headers[name]) headers[name]=req.headers[name];
    const response=await fetch(upstream+req.url,{method:req.method,headers,body:['GET','HEAD'].includes(req.method)?undefined:Buffer.concat(parts),redirect:'manual',signal:AbortSignal.timeout(10000)});
    const body=Buffer.from(await response.arrayBuffer());
    if(!dropped && req.method==='POST' && response.ok){dropped=true;req.socket.destroy();console.log('Dropped one successful POST response after upstream completion.');return;}
    const outgoing={}; for(const name of ['content-type','location','x-request-id']) if(response.headers.has(name)) outgoing[name]=response.headers.get(name);
    res.writeHead(response.status,outgoing).end(body);
  } catch { if(!res.headersSent) res.writeHead(502);res.end(); }
});
server.listen(18080,'127.0.0.1',()=>console.log('Lost-response fixture on 127.0.0.1:18080; first successful POST response will be dropped.'));
