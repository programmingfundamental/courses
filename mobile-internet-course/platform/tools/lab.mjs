import { spawnSync } from 'node:child_process';
import { fileURLToPath } from 'node:url';
import path from 'node:path';
const platform = fileURLToPath(new URL('../', import.meta.url));
const [lab, action='build'] = process.argv.slice(2);
if (!/^\d{2}$/.test(lab) || Number(lab)<1 || Number(lab)>10) throw new Error('Lab must be 01..10');
const androidLabs = ['01','02','08','09'];
function run(command,args,cwd) {
  const result=spawnSync(command,args,{cwd,stdio:'inherit',shell:process.platform==='win32'});
  if(result.error) throw result.error;
  if(result.status!==0) process.exit(result.status ?? 1);
}
if(action==='build') {
  if(androidLabs.includes(lab)) {
    const tasks=[':app:assembleDebug',':app:testDebugUnitTest'];
    run(process.platform==='win32'?'gradlew.bat':'sh',process.platform==='win32'?tasks:['./gradlew',...tasks],path.join(platform,'android'));
  }
  if(lab!=='01') run('mvn',['-B','verify'],path.join(platform,'backend'));
} else if(action==='up') {
  run('node',['tools/token.mjs','--init'],platform);
  run('docker',['compose','up','-d','--build'],platform);
} else if(action==='config') run('docker',['compose','config','--quiet'],platform);
else throw new Error('Use build, up or config.');
