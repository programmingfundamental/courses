param([ValidateRange(1024,65535)][int]$Port=8080)
# Positive/negative flows през реалния локален proxy. Само secure mode.
$ErrorActionPreference='Stop'
$base="http://localhost:$Port"
function Expect-Status([scriptblock]$Action,[int]$Expected) {
    try { $result=& $Action; $actual=[int]$result.StatusCode }
    catch { if(-not $_.Exception.Response){throw}; $actual=[int]$_.Exception.Response.StatusCode }
    if($actual -ne $Expected){throw "Expected HTTP $Expected, got $actual"}
}
$health=Invoke-RestMethod "$base/health"
if($health.status -ne 'UP'){throw 'Health mismatch'}
Expect-Status { Invoke-WebRequest "$base/api/me" -UseBasicParsing } 401
$csrf=Invoke-RestMethod "$base/csrf" -SessionVariable session
Expect-Status { Invoke-WebRequest "$base/login" -Method Post -Body @{username='alice';password='Lab-alice-2026!';_csrf=$csrf.token} -WebSession $session -UseBasicParsing } 204
$csrf=Invoke-RestMethod "$base/csrf" -WebSession $session
$headers=@{}; $headers[$csrf.headerName]=$csrf.token
$me=Invoke-RestMethod "$base/api/me" -WebSession $session
if($me.username -ne 'alice'){throw 'Wrong identity'}
$own=Invoke-RestMethod "$base/api/documents/1" -WebSession $session
if($own.owner -ne 'alice'){throw 'Wrong owner'}
Expect-Status { Invoke-WebRequest "$base/api/documents/2" -WebSession $session -UseBasicParsing } 404
Expect-Status { Invoke-WebRequest "$base/admin/status" -WebSession $session -UseBasicParsing } 403
$query=[Uri]::EscapeDataString("' OR '1'='1' -- ")
$found=Invoke-RestMethod "$base/api/search?q=$query" -WebSession $session
if(@($found).Count -ne 0){throw 'SQL injection regression'}
$before=(Invoke-WebRequest "$base/profile" -WebSession $session -UseBasicParsing).Content
Expect-Status { Invoke-WebRequest "$base/api/profile" -Method Post -Body @{displayName='forged'} -WebSession $session -UseBasicParsing } 403
$after=(Invoke-WebRequest "$base/profile" -WebSession $session -UseBasicParsing).Content
if($before -ne $after){throw 'Rejected request changed state'}
Expect-Status { Invoke-WebRequest "$base/api/comments" -Method Post -Body @{body="<script>document.title='LAB-XSS'</script>"} -Headers $headers -WebSession $session -UseBasicParsing } 201
$comments=Invoke-WebRequest "$base/comments" -WebSession $session -UseBasicParsing
if($comments.Content -notmatch '&lt;script&gt;' -or $comments.Content -match '<script>'){throw 'Unsafe output'}
if(-not $comments.Headers['Content-Security-Policy']){throw 'Missing CSP'}
Expect-Status { Invoke-WebRequest "$base/api/sensitive" -Method Post -Body @{value='SYNTHETIC-SMOKE'} -Headers $headers -WebSession $session -UseBasicParsing } 200
$field=Invoke-RestMethod "$base/api/sensitive" -WebSession $session
if($field.value -ne 'SYNTHETIC-SMOKE'){throw 'Crypto roundtrip failed'}
$token=Invoke-RestMethod "$base/token" -Method Post -Headers $headers -WebSession $session
$resource=Invoke-RestMethod "$base/token-api/documents" -Headers @{Authorization="Bearer $($token.access_token)"}
if($resource.subject -ne 'alice'){throw 'JWT identity mismatch'}
Expect-Status { Invoke-WebRequest "$base/token-api/documents" -WebSession $session -UseBasicParsing } 401
Expect-Status { Invoke-WebRequest "$base/logout" -Method Post -Headers $headers -WebSession $session -UseBasicParsing } 204
Expect-Status { Invoke-WebRequest "$base/api/me" -WebSession $session -UseBasicParsing } 401
Write-Output 'PASS: proxy, PostgreSQL-backed login, ownership, SQLi, XSS encoding/CSP, CSRF state, crypto, JWT and logout.'
