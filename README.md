# Real estate data scraper

[This repository](https://github.com/Tale152/real_estate_data_scraper) contains a module developed for the third Assignment for the course of Big Data Management (T-764-DATA) taken in Reykjavìk University during the Spring semester 2022.  
This project is developed by: [Alessandro Talmi](https://github.com/Tale152) and [Elisa Tronetti](https://github.com/ElisaTronetti).

## Complete Project

The complete project consists of:
- pick the theme of the resource;
- estimate and analyze the data sources (data format, quantity, quality, ...) and identify appropriate solutions for collecting, processing and storing the data;
- propose example use-cases for deriving information and value from the proposed resource. This be pre-process or post-process and should include identifying alhorithms or solutions that can derive information from the raw data.

The theme chosen of the project developed regards the real estates analysis in Italy.

### Scraping Module

In this repository can be found the implementation of a Data Scraper, which is used to extract data from web sites.  
In the current implementation the Scraper is only able to extract data from [Immobiliare.it](https://www.immobiliare.it/), which is one of the biggest web sites for real estate selling and renting in Italy.  
The design of the solution is made in order to give the opportunity to easily add other implementation for Scraping from other web sites.  

## Technical Setup

The setup is the following:
- Java version >= **1.8**
- Scala version >= **2.12.10**
- Sbt version >= **1.5.5**

## How To Run

It is possible to run the application via sbt using the following code:
```
sbt "run -s=<DATA SOURCE> -t=<THREAD NUMBER> -d=<DATE>"
```

The arguments are mandatory and these are their use:
- **-s** is used to specify the data source where the scraping is going to be performed. This argument is mainly useful if more than one data source is added; in the current implementation, the only possible value for this argument is __immobiliare.it__.
- **-t** accepts only an integer and it is used to specify the number of threads that are going to be used to retrieve data.
- **-d** accepts a date in the format __DD/MM/YYYY__. The application is going to retrieve data from the current date until the date specified as argument; once reached an advertisment older than the data specified, the application stops retrieving data.

## Code Architecture
The Main is used to retrieve the arguments provided, to create the directory for the results if not already present and to run the actual computation and to close the file writer after the computation is finished.  

It has been decided to use the *Executor** to run the tasks, in order to have more threads (the number of thread is specified by the input argument **-t**) that are going to allow the application to be more efficient.  
This behaviour is in **scala.scraping.ScrapingExecutors**. It is created a bag of tasks, which is composed by Task created based on the data source: in the case of Immobiliare.It, how the bag of tasks is created is in **scala.immobiliareIt.ImmobiliareIt**.  
Once all the house sellings and rentings have been found, the Executor is used to invoke all the tasks.  

At the moment, the tasks of Immobiliare.it are the only ones available. There are two different tasks:
- **HtmlAvailableTask**</br>
This task is used when the html of an house has been already d: this can happen when the html has been requested in order to check if the date is in the possible range or if it has to be discarded because it is too old.  
This choice is made to improve performances, because the html is not requested again when all the data of the house selling/renting has to be scraped. This avoid at least one request per page checked.

- **CompleteTask**</br>
This task is very similar to the previous one, and the only difference is that it is required one step more: in this case the html is not available yet, so it has to be retrieved. After that, the HtmlAvailableTask can be called.  

The final step that has to be discussed is how the data is actually saved.  
Once the information from an house selling/renting is completed, it is saved in a File, created by the Main.  
This is made at the end of the HtmlAvailableTask, using a data structure called **scala.scraping.ResultsHandler**.  
It must be kept in mind that having multiple thread trying to write on a single File is a problem, because it will cause concurrency issues.  
For this reason it has been implemented a simple locking mechanism for the lock and unlock of the File, in order to ensure that only one thread at the time will perform a write operation on the File. This is made using the mutex lock and unlock provided by Java.  
Every time the **put** operation of the **ResultsHandler** is called, the result of the task is saved in a buffer: if the buffer has more thant 400 records, these are going to be appended on the File.  
Once the computation of all the tasks is completed, it is called a **complete** operation, which is going to empty the buffer with all the records left.  

Finally, all the results are visible in the File: it is a valid Json file, so it can be analyzed afterword if needed.