param([string]$UserName='alice', [string]$Password='Lab-alice-2026!')
# Dot-source: . ./scripts/lab-client.ps1 ; exposes $LabSession and $LabHeaders.
$ErrorActionPreference='Stop'
$base='http://localhost:8080'
$csrf=Invoke-RestMethod "$base/csrf" -SessionVariable LabSession
$login=@{username=$UserName;password=$Password}
if($csrf.token) { $login['_csrf']=$csrf.token }
Invoke-WebRequest "$base/login" -Method Post -Body $login -WebSession $LabSession -UseBasicParsing | Out-Null
$csrf=Invoke-RestMethod "$base/csrf" -WebSession $LabSession
$LabHeaders=@{}
if($csrf.token) { $LabHeaders[$csrf.headerName]=$csrf.token }
Write-Output 'Session ready at localhost:8080; CSRF token refreshed after login.'
