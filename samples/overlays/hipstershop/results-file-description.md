## Service metrics

Each service has three metrics to track its performance: success, failures, and success rate. 
These metrics are represented by the service name followed by one of the following suffixes: `_error`, `_success`, and `_success_rate`.


| Name                        | Type           | Description                                                                                   |
|-----------------------------|----------------|-----------------------------------------------------------------------------------------------|
| service_name_error          | Counter        | Counts the number of errors occurring in requests to the service.                             |
| service_name_success        | Counter        | Counts successful requests to the service.                                                    |
| service_name_success_rate   | Rate           | Measures the success rate for the service requests.                                           |


## Load testing metrics

These metrics are derived from the k6 load testing tool. It's possible to include any metric available [here](https://grafana.com/docs/k6/latest/using-k6/metrics/reference/).

| Name                        | Type           | Description                                                                                   |
|-----------------------------|----------------|-----------------------------------------------------------------------------------------------|
| iterations                  | Counter        | The aggregate number of times the VUs execute the JS script (the default function). |
| iteration_duration          | Trend          | The time to complete one full iteration, including time spent in setup and teardown.          |
| http_reqs                   | Counter        | How many total HTTP requests k6 generated. |

## Configuration metrics

Represents the configuration used for the test results.

| Name                        | Type           | Description                                                                                   |
|-----------------------------|----------------|-----------------------------------------------------------------------------------------------|
| FAULT_PERCENTAGE            | Integer        | Specifies the percentage of faults to introduce in the service for resilience testing.        |
| GRPC_BACKOFF_MULTIPLIER     | Integer        | Sets the backoff multiplier for gRPC retry behavior.                                          |
| GRPC_INITIAL_BACKOFF        | Integer        | Defines the initial backoff duration for gRPC retries.                                        |
| GRPC_MAX_ATTEMPTS           | Integer        | Specifies the maximum number of retry attempts for gRPC calls.                                |
| GRPC_MAX_BACKOFF            | Integer        | Sets the maximum backoff time for gRPC retries.                                               |
