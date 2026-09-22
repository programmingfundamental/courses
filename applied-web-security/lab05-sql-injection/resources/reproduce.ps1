# Изпълнение от корена на курса. Само synthetic seed documents.
. ./scripts/lab-client.ps1
$query=[Uri]::EscapeDataString("' OR '1'='1' -- ")
Invoke-RestMethod "http://localhost:8080/api/search?q=$query" -WebSession $LabSession
# lab05: присъства owner=bob; поправен: празен резултат.
