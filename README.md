# Chessmates ![Build Status](https://travis-ci.org/bjedrzejewski/chessmates.svg?branch=master)
   Statistics for your Lichess team 
   
   ## Summary
   
   The backend service schedules a task to periodically scrape the Lichess API for data. This data is stored in its entirety
    and will be used in the future to provide richer statistical analysis team games.
    
   ## Tech
   
   The application is **Spring Boot** written in **Groovy**, tested using **Spock** and build with **Maven**.
   
   ## Info
    
   ### Fetching Lichess Data
    
   Due to certain restrictions set by Lichess.com, we have to be careful to limit the number of requests we make as they 
    error requests with 429 quite liberally. This is made more difficult by the fact that the Lichess API only gives us the
    ability to request games between two players, and limits the number of games returned on a page of data to 100, meaning
    we have to make multiple request per set of opponents within the team.
    
   We're also relying on some pretty brittle assumptions about the Lichess API. To cut down requests for data we've already
   fetched and persisted, we keep track of latest values we've retrieved from each *data set*. When we start to request that
   *data set* again, we check each item in each page, and when encounter the most recent *item* we've already fetched, we know
    we can stop making request. 
    
   **This is clearly brittle because we're relying on the ordering of items returned by Lichess' API -
    of which there is no stated guarentee.**
    
    Terminology
    * Data set - e.g 'Scott Logic players' or 'Games between UserA & UserB'
    * item - Player or Game
   
   ## Local Installation
   
   ### IntelliJ
   
   1. Set up the password to the SSL certificate.
       1. Check the [wiki page](https://int.scottlogic.co.uk/wiki/Chessmates) (Internal Scott Logic wiki) for the SSL password.
       1. Set the `CHESSMATES_KEYSTORE_PW` environment variable to this value.(`Build, Execution & Deployment > Build Tools > Maven > Runner > Environment Variables`)
   
  
   ## Running the application
   
   Run the maven task `spring-boot:run` to start up the application in Spring Boot's embedded Tomcat server.
    
   ## Contributing
   
   Fork the [GitHub repository](https://github.com/bjedrzejewski/chessmates) & make a pull request.
   
   ## Raising issues
   
   On the [GitHub repository](https://github.com/bjedrzejewski/chessmates).