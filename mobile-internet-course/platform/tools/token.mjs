import fs from 'node:fs';
import { generateKeyPairSync, createSign } from 'node:crypto';
import { fileURLToPath } from 'node:url';
const dir = fileURLToPath(new URL('../.runtime/keys/', import.meta.url));
fs.mkdirSync(dir, { recursive: true });
if (!fs.existsSync(dir + '/private.pem')) {
  const keys = generateKeyPairSync('rsa', { modulusLength: 2048, publicKeyEncoding: {type:'spki',format:'pem'}, privateKeyEncoding: {type:'pkcs8',format:'pem'} });
  fs.writeFileSync(dir + '/private.pem', keys.privateKey, {mode:0o600});
  fs.writeFileSync(dir + '/publicKey.pem', keys.publicKey);
}
if (process.argv.includes('--init')) { console.log('Local demo keys ready.'); process.exit(0); }
const role = process.argv[2] ?? 'member';
if (!['member','viewer'].includes(role)) throw new Error('Use member or viewer.');
const now = Math.floor(Date.now()/1000);
const ttl = process.argv.includes('--expired') ? -120 : 300;
const encode = value => Buffer.from(JSON.stringify(value)).toString('base64url');
const unsigned = encode({alg:'RS256',typ:'JWT'}) + '.' + encode({iss:'https://mobile-lab.invalid',aud:process.argv.includes('--wrong-audience')?'other-api':'mobile-api',sub:process.argv.includes('--other-user')?'u2':'u1',upn:'student',groups:[role],iat:now-180,exp:now+ttl});
const signer = createSign('RSA-SHA256').update(unsigned).end();
console.log(unsigned + '.' + signer.sign(fs.readFileSync(dir + '/private.pem'),'base64url'));
