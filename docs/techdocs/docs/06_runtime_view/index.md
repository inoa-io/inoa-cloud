# Runtime View

??? tip

    **Contents**

    The runtime view describes concrete behavior and interactions of the
    system's building blocks in form of scenarios from the following areas:

    - important use cases or features: how do building blocks execute
    them?
    - interactions at critical external interfaces: how do building blocks
    cooperate with users and neighboring systems?
    - operation and administration: launch, start-up, stop
    - error and exception scenarios
    Remark: The main criterion for the choice of possible scenarios
    (sequences, workflows) is their **architectural relevance**. It is
    **not** important to describe a large number of scenarios. You should
    rather document a representative selection.

    **Motivation**

    You should understand how (instances of) building blocks of your system
    perform their job and communicate at runtime. You will mainly capture
    scenarios in your documentation to communicate your architecture to
    stakeholders that are less willing or able to read and understand the
    static models (building block view, deployment view).

    **Form**

    There are many notations for describing scenarios, e.g.

    - numbered list of steps (in natural language)
    - activity diagrams or flow charts
    - sequence diagrams
    - BPMN or EPCs (event process chains)
    - state machines
    - ...

    See [Runtime View](https://docs.arc42.org/section-6/) in the arc42
    documentation.

## Collecting measured Data via INOA Satellites

![Telemetry Collection](dataflows.drawio-telemetry-dark.svg)

This is the main use case of INOA:

We measure and collect data from distributed sensors and meters, transfer it into INOA Cloud, translate them into readable values and store them into a time series database.

Here are the steps in detail:

1. INOA Satellite has datapoints collected and send them via MQTT into INOA Cloud.
2. The Gateway Service authenticates the Satellite and retrieves the send data.
3. The Gateway Service publishes the retrieved raw data into Kafka topic `hono.telemetry.<tenant>`
4. The Translator is listening at the topic `hono.telemetry.<tenant>` and retrieves the raw data.
5. The Translator service translates the retrieved raw data into a measurement(s).
6. The Translator publishes the measurement(s) in Kafka topic `inoa.telemetry.<tenant>`
7. Read measurements

   1. The InfluxDbExporter is listening at the topic `inoa.telemetry.<tenant>`
   2. The InfluxDbExporter is writing the measured data into the InfluxDb.


## Configuration of Satellites

t.b.d.

## Definition of measured data

t.b.d.
