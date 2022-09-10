## Running the project

To run the project, you need to run the server and the frontend.

### Running the server

In the terminal, run `./gradlew narcore-server:run`

### Running the frontend

In the terminal, run `./gradlew :narcore-web:run -t`

## Understanding the codebase

### Naming Suffixes

objectTable -> DB table
objectDto -> Data Transfer Object
objectDs -> Data Structure (e.g. POJO, POKO)
objectRm -> Repository Model (Normally a composition of multiple models)

### Data Transfer Objects (DTOs)

DTOs are placed in a common project and shared between the front end and the backend.

### DAO vs Repositories

A DAO correspond to a single Table (and a single model) and implement CRUD operations.

Repository is similar to a DAO Facade (Read about the Facade Design Pattern) and it abstracts the access to multiple
DAOs. However, it has a single "fat" model. Normally, the repo fat model is a composition of the models of the DAOs.

For example:

ClientsDao -> ClientsTable
UsersDao -> UsersTable
RolesDao -> RolesTable

Then:

UsersRepository -> ClientsDao, UsersDao, RolesDao
