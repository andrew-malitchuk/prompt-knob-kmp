# data-core — Agent Guide

Leaf module. Defines the single `Resource` marker interface that every data-layer representation (preference models, database entities, BLE/command resources) implements.

## Responsibility

- `data.core.source.resource.Resource` — empty marker interface. Its only job is to give data-layer types a shared upper bound so mappers and generic code can constrain to it.

## Conventions

- Applies `dev.prompt.knob.io.convention.library`.
- Explicit API mode is on.
- No internal project dependencies — keep it a leaf.

## Gotchas

- Do not add behaviour or fields to `Resource`; it is deliberately a bare marker.
- Domain models do NOT implement `Resource` — that boundary is intentional. `Resource` is data-layer only; mapping between the two happens in `data-repository-impl`.
