# Fetch_SRE_Exercise

## Overview
Hey there! This project is a simple health check program I put together to monitor the availability of a set of HTTP endpoints. It reads from a configuration file in YAML format, checks the health of each endpoint every 15 seconds, and logs the availability percentage to the console. I wanted to create something that could keep track of uptime in an automated way, and this seemed like a good fit.

## Features
- Reads a YAML configuration file for a list of HTTP endpoints to monitor.
- Sends HTTP requests every 15 seconds to check the health of each endpoint.
- Considers an endpoint healthy if it returns a 2xx status code and responds in under 500ms.
- Logs the cumulative availability percentage for each endpoint as long as the program runs.

## Requirements
- Java 11 or higher
- Maven (for dependency management)

## Setup Instructions

1. **Clone the Repository**:
   ```sh
   git clone <repository-URL>
   cd Fetch_SRE_Assessment
   ```

2. **Build the Project**:
   Make sure you have Maven installed, then run the following command to build the project:
   ```sh
   mvn clean install
   ```

3. **Create Configuration File**:
   You'll need a YAML configuration file (`sample_input_config.yaml`) in `src/main/resources/` like this:
   ```yaml
   - headers:
       user-agent: fetch-synthetic-monitor
     method: GET
     name: fetch index page
     url: https://fetch.com/

   - headers:
       user-agent: fetch-synthetic-monitor
     method: GET
     name: fetch careers page
     url: https://fetch.com/careers

   - body: '{"foo":"bar"}'
     headers:
       content-type: application/json
       user-agent: fetch-synthetic-monitor
     method: POST
     name: fetch some fake post endpoint
     url: https://fetch.com/some/post/endpoint

   - name: fetch rewards index page
     url: https://www.fetchrewards.com/
   ```

4. **Run the Application**:
   To run the application, use:
   ```sh
   java -cp target/Fetch_SRE_Assessment-1.0-SNAPSHOT.jar org.fetch.HealthCheck
   ```
   This will start the health check on the endpoints defined in the configuration file.

## Usage Notes
- The program runs continuously, checking the health of each endpoint every 15 seconds.
- It calculates availability based on the percentage of successful (`UP`) checks vs. total checks made.
- To stop the program, simply use `CTRL+C`.

## Logging
- The application logs the availability percentage of each domain to the console after each test cycle. Here's an example of what you might see:
  ```
  fetch.com has 67% availability percentage
  www.fetchrewards.com has 50% availability percentage
  ```

## Contributing
If you'd like to contribute:
1. Fork the repository.
2. Create a feature branch (`git checkout -b feature-name`).
3. Commit your changes (`git commit -m 'Add new feature'`).
4. Push to the branch (`git push origin feature-name`).
5. Open a pull request.





