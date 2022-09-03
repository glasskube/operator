# Contributing guide for the Glasskube Operator

Welcome, and thank you for deciding to invest some of your time in contributing to the Glasskube project!
The goal of this document is to define some guidelines to streamline our contribution workflow.

## Issues

Take a look at our issues board if you want to learn about current tasks.

## Making changes

1. If you want to submit a change, start by searching for a related issue or creating a new one.
   Please, let us know what you are working on so we are able to give feedback as early as possible.
2. Fork this repository and check out your fork.
3. Create a working branch.
4. Start working on your changes.
5. Commit your changes (see below).
6. Create a pull request, once you feel ready.

## Committing a Change

We require all commits in this repository to adhere to the following commit message format.

```
<type>: <description> (#<issue number>)

[optional body]
```

The following `<type>`s are available:

* `fix` (bug fix)
* `feat` (includes new feature)
* `docs` (update to our documentation)
* `build` (update to the build config)
* `perf` (performance improvement)
* `style` (code style change without any other changes)
* `refactor` (code refactoring)
* `chore` (misc. routine tasks; e.g. dependency updates)

This format is based on [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/).
Please refer to the Conventional Commits specification for more details.
