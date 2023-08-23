---
sidebar_position: 1
---

# Metabase

Metabase is an open-source business intelligence and data analytics platform that simplifies data analysis and visualization. 
It connects to various data sources, including SQL databases and CSV files, making it accessible to both technical and non-technical users.
Metabase offers pre-built dashboards, customization options, and data management features for insightful decision-making. 

## Spec

| Name      | Type                                                                                                   | Default    |
|-----------|--------------------------------------------------------------------------------------------------------|------------|
| host      | String                                                                                                 | (required) |
| replicas  | Int                                                                                                    | 1          |
| smtp      | [SmtpSpec](common/smtp)?                                                                                              | `null`     |
| resources | [ResourceRequirements](https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/) |            |
