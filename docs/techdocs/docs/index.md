
# Overview

With the structure of the [Arc42](https://arc42.org/), the software architecture of [INOA](https://www.inoa.io) is explained on the following
pages.

## 1. [Introduction and Goals](01_introduction_and_goals/index.md)

Short description of the requirements, driving forces, extract (or abstract) of requirements. Top three (max five) quality goals for the architecture which have the highest priority for the major stakeholders. A table of important stakeholders with their expectation regarding architecture.

![](https://arc42.org/images/template/01-intro-and-goals.png)

##  2. [Constraints](02_architecture_constraints/index.md)

Anything that constrains teams in design and implementation decisions or decision about related processes. Can sometimes go beyond individual systems and are valid for whole organizations and companies.

![](https://arc42.org/images/template/02-constraints-overview.png)

##  3. [Context and Scope](03_system_scope_and_context/index.md)

Delimits your system from its (external) communication partners (neighboring systems and users). Specifies the external interfaces. Shown from a business/domain perspective (always) or a technical perspective (optional).

![](https://arc42.org/images/template/03-context-overview.png)

##  4. [Solution Strategy](04_solution_strategy/index.md)

Summary of the fundamental decisions and solution strategies that shape the architecture. Can include technology, top-level decomposition, approaches to achieve top quality goals and relevant organizational decisions.

![](https://arc42.org/images/template/04-solution-strategy-overview.png){ width="100" }

##  5. [Building Block View](05_building_block_view/index.md)

Static decomposition of the system, abstractions of source-code, shown as hierarchy of white boxes (containing black boxes), up to the appropriate level of detail.

![](https://arc42.org/images/template/05-building-block-overview.png)

##  6. [Runtime View](06_runtime_view/index.md)

Behavior of building blocks as scenarios, covering important use cases or features, interactions at critical external interfaces, operation and administration plus error and exception behavior.

![](https://arc42.org/images/template/06-runtime-overview.png)

##  7. [Deployment View](07_deployment_view/index.md)

Technical infrastructure with environments, computers, processors, topologies. Mapping of (software) building blocks to infrastructure elements.

![](https://arc42.org/images/template/07-deployment-overview.png)

##  8. [Crosscutting Concepts](08_concepts/index.md)

Overall, principal regulations and solution approaches relevant in multiple parts (→ cross-cutting) of the system. Concepts are often related to multiple building blocks. Include different topics like domain models, architecture patterns and -styles, rules for using specific technology and implementation rules.

![](https://arc42.org/images/template/08-concepts-overview.png)

##  9. [Architectural Decision](09_architecture_decisions/index.md)

Important, expensive, critical, large scale or risky architecture decisions including rationales.

![](https://arc42.org/images/template/09-decision-overview.png)

##  10. [Quality Requirements](10_quality_requirements/index.md)

Quality requirements as scenarios, with quality tree to provide high-level overview. The most important quality goals should have been described in section 1.2. (quality goals).

![](https://arc42.org/images/template/10-q-scenario-overview.png)

##  11. [Risks and Technical Debts](11_technical_risks/index.md)

Known technical risks or technical debt. What potential problems exist within or around the system? What does the development team feel miserable about?

![](https://arc42.org/images/template/11-risk-overview.png){ width="100" }

##  12. [Glossary](12_glossary/index.md)

Important domain and technical terms that stakeholders use when discussing the system. Also, translation reference if you work in a multi-language environment.

![](https://arc42.org/images/template/12-glossary-overview.png)
