# Насоки за преподавателя — курс

## Предварителни знания и учебни цели

Предполага се Python, основи на ML/Jupyter, Git, REST, Docker, scikit-learn/pandas/numpy, Linux и бази данни. След курса студентът може да формулира measurable requirements, да разделя training/inference, да refactor-ва coupling, да избира design patterns, да изгражда test strategy и CI/CD, да управлява data/model lineage и rollback и да аргументира observability/security/ethics/maintenance решения.

```text
Notebook → Engineering Process → Requirements → Architecture → Modularity
→ Design → Testing → CI/CD → MLOps → Observability → Security + Maintenance
```

Педагогическият модел във всеки lab е **проблем → теория → анализ на лошо решение → практическа задача → самостоятелна задача → тестове → инженерна дискусия**. Student files дават стъпки и acceptance criteria, без пълно решение; instructor-notes са отделни. Общият `ai-platform` е runnable reference baseline и база за последователни student increments. Starter TODO задачите изискват собствен diff и нови tests, а не просто стартиране на reference suite.

