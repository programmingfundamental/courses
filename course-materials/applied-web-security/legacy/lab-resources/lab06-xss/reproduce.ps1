# Само harmless title marker, без network/exfiltration.
. ./scripts/lab-client.ps1
Invoke-RestMethod http://localhost:8080/api/comments -Method Post `
    -WebSession $LabSession -Headers $LabHeaders `
    -Body @{body="<script>document.title='LAB-XSS'</script>"}
# Отворете localhost:8080/comments в authenticated browser.
