# Environments

## Context

This concept explains:

1. Which environments we have?
2. How we configure them?
3. How we map environments to URLs / Hostnames?
4. How we map environments to Kubernetes?
5. How we roll out INOA (staging concept)?

## INOA Environments

| No |     Environment      |                                  Description                                   |    Domain     | k8s Namespace |
|---:|----------------------|--------------------------------------------------------------------------------|---------------|---------------|
| 1. | **Developer PC**     | The place where we develop the components of INOA                              | *.inoa.local  | default       |
| 2. | **Integration Test** | The short living environments for testing INOA integrated with other services. | *.inoa.local  | default       |
| 3. | **Staging**          | Our pre-productive environment that is used for testing and validation.        | *.dev.inoa.io | inoa-dev      |
| 4. | **Production**       | Our productive environment that is used for all productive set-ups.            | *.inoa.io     | inoa-prod     |
