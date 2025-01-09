# Simple File-Based Relational-Like Database

## Overview
This project implements a database that simulates relational database functionality. It allows for the creation, management, and persistence of tables using a flat file storage system.
The database supports basic operations such as inserting, updating, selecting, and deleting rows within tables.

## Features
- **Table Management**: Create and drop tables dynamically, with immediate persistence to disk.
   Each table is represented as a text file, and files are created even if the table is initially empty, ensuring consistency between in-memory and on-disk data.
- **Data Persistence**: Save tables to disk in text files, ensuring data is retained across application runs.
   The persistence has been enhanced to ensure that tables are saved at the point of creation, even if no rows are present yet.
- **Basic SQL-like Operations**:
  - Insert rows into tables.
  - Update existing rows.
  - Delete rows from tables.
  - SelectByName or Id and display rows from tables.
- **REST API Access**: A RESTful interface was created using Spring Boot, allowing for CRUD operations on the database through HTTP requests.
-  Endpoints are available for creating tables, inserting rows, updating records, deleting rows, and listing tables, making the database accessible via REST API for remote interaction.

## Project Stages
The project goes through five stages; this implementation represents the first three stages:

- **Lab 1: Interfaces for the Database**: Created interfaces for core classes, defining the skeleton of the program, including `ITableManager`, `ITable`, and `IQueryParser` interfaces.
- **Lab 2: Implementation of Interfaces**: Implemented the defined interfaces to build a locally working database that can create, manage, and persist tables.
- **Lab 3: Database Deployment and REST API Access**: Extended the functionality by adding a RESTful interface to allow interaction with the database through HTTP endpoints. This stage also involved deploying the database on a local host, enabling access through REST, even on a local machine.
