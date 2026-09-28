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
    if($studentText -notmatch '(?s)^# [^\r\n]+\r?\n\s*## 1\. Теория'){throw "${id}: theory must follow the title"}
    foreach($heading in @('## 2. Подготовка','## 3. Примерен проблем','### Стъпки за решаване','## Самостоятелни задачи')){
        if(-not $studentText.Contains($heading)){throw "${id}: missing $heading"}
    }
    $hint=Join-Path $lab.FullName 'hint.md'
    $hintText=Get-Content -LiteralPath $hint -Raw
    if($hintText -notmatch '## Въпроси за анализ' -or $hintText -notmatch '## Checklist'){throw "${id}: missing instructor hints"}
    if($studentText -match '## (?:Въпроси за анализ|Checklist)|Продължителност:|Аудитория:|умишлен учебен дефект'){throw "${id}: unexpected student annotation"}
    if(([regex]::Matches($notesText,'(?m)^## \d+\. ')).Count -ne 16){throw "${id}: expected 16 instructor sections"}
    if(-not (Test-Path (Join-Path $lab.FullName 'resources/README.md'))){throw "Missing resources: $id"}
}
Write-Output 'PASS: 10 labs with theory, worked examples, independent tasks, separate hints, instructor notes and resources.'
