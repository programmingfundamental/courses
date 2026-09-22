$ErrorActionPreference='Stop'
$courseRoot=Split-Path -Parent $PSScriptRoot
$labs=@(Get-ChildItem -LiteralPath $courseRoot -Directory | Where-Object Name -Match '^lab\d{2}-')
if($labs.Count -ne 10){throw "Expected exactly 10 labs, got $($labs.Count)"}
foreach($lab in $labs){
    $id=$lab.Name.Substring(0,5)
    $student=Join-Path $lab.FullName "$id.md"
    $notes=Join-Path $lab.FullName 'instructor-notes.md'
    $studentText=Get-Content -LiteralPath $student -Raw
    $notesText=Get-Content -LiteralPath $notes -Raw
    if(([regex]::Matches($studentText,'(?m)^#{1,2} \d+\. ')).Count -ne 17){throw "${id}: expected 17 sections"}
    if(([regex]::Matches($notesText,'(?m)^## \d+\. ')).Count -ne 16){throw "${id}: expected 16 instructor sections"}
    if(-not (Test-Path (Join-Path $lab.FullName 'resources/README.md'))){throw "Missing resources: $id"}
    if($studentText -notmatch 'единствено срещу предоставената локална лабораторна среда'){throw "Missing safety scope: $id"}
}
Write-Output 'PASS: 10 labs, 17 student sections each, 16 instructor sections each, resources and local-only scope.'
