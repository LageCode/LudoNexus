# LudoNexus - Technical Documentation

## Overview

LudoNexus is a suite of two microservices built with Spring Boot that manage players and game sessions for online multiplayer games:

- PlayerSphere: Handles player information, statistics, and friendships
- BattleForge: Manages game sessions and player participation

## Project Scope

LudoNexus provides backend services for:

- 💾 Player data storage and retrieval
- 🕹️ Game session management
- 📈 Player statistics updates
- 🧑‍🍳 REST-based service communication

### Features

## PlayerSphere Service

1. Player Management
   - Create, read, update, delete player profiles
   - Store player information (username, email)
   - Track player level and points
2. Friend System
   - Add and remove friends
   - Maintain friend relationships
   - Retrieve friend lists

## BattleForge Service

1. Game Management
   - Create and manage game sessions
   - Configure game settings
   - Track host player
   - Set maximum scores
2. Participation Tracking
   - Record player participation
   - Track individual scores
   - Monitor victory status
   - Handle game completion
3. Statistics Management
   - Track and update players total points
   - Manage players level progression
   - Record players game performance

### Technical Constraints

1. Microservice Architecture

   - Independent services

     - Data models

       - DTOs

         - RequestDTOs: Objects for incoming API requests
         - ResponseDTOs: Objects for API responses
         - Internal DTOs: Objects for inter-service communication

         > Note: Internal DTOs are designed to serve both inter-service communication and API responses, eliminating the need for separate Response DTOs in most cases. This approach reduces duplication and maintains consistency across the system.

   - REST communication between PlayerSphere and BattleForge

   - No shared libraries

   - Unified PostgreSQL database with logical service separation

2. Data Structure

- PlayerSphere:

  - Player: id, name, username, email, level, total_points

  - Friend: id, player_id, friend_id

- BattleForge:
  - Game: id, date, game_type, max_score, host_id
  - Participation: id, game_id, player_id, score, victory

3. Required Endpoints

   - PlayerSphere:
     ```
     POST /players
     GET /players/{id}
     POST /players/{id}/friends
     ```


   - BattleForge:
     ```
     POST /games
     POST /games/{id}/participations
     GET /games/{id}
     ```

4. Technical Stack

   - Spring Boot 3.2.0

   - Java 21

   - PostgreSQL

   - Layered Architecture:
     - Controllers (REST endpoints)
     - Services (Business logic)
     - Repositories (Data access)
     - DTOs (Data transfer)
     - Entities (Data model)

## Getting Started

### Prerequisites

1. **Java Development Kit (JDK)**

    ```bash
    # Ubuntu/Debian
    sudo apt install openjdk-21-jdk
    
    # Verify installation
    java --version  # Should show 21.0.x
    ```

2. **PostgreSQL**

    ```bash
    # Ubuntu/Debian
    sudo apt update
    sudo apt install postgresql postgresql-contrib
    
    # Start PostgreSQL service
    sudo service postgresql start
    ```

3. **Maven**

    ```bash
    # Ubuntu/Debian
    sudo apt install maven
    
    # Verify installation
    mvn --version
    ```

### Dependencies

The project uses the following dependencies:

- Spring Boot Parent (v3.4.0)

- Spring Boot Web Starter
- Spring Data JPA Starter
- Spring Boot Validation Starter
- PostgreSQL Driver
- Lombok (v1.18.30)

Add these to your `pom.xml`:

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.0</version>
</parent>

<dependencies>
    <!-- Spring Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Spring Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- PostgreSQL Driver -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>
    
    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.30</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### Database Setup

#### User and Database

Connect as postgres superuser:

```bash
sudo -u postgres psql
```

Create the developer user and the databases:

```postgresql
CREATE USER lunedev WITH PASSWORD 'lunedev';
CREATE DATABASE ludonexusdb OWNER lunedev;
```

> Names given here are important because they should match `application.properties` files

#### Tables

- Connect to LudoNexus Database as LudoNexus Developer user:

    ```postgresql
    \connect postgresql://lunedev:lunedev@localhost:5432/ludonexusdb
    ```
  
- Create players and friendships tables:

    ```postgresql
    DROP TABLE IF EXISTS participations;
    DROP TABLE IF EXISTS games;
    DROP TABLE IF EXISTS friendships;
    DROP TABLE IF EXISTS players;
    
    CREATE TABLE players (
        id BIGSERIAL PRIMARY KEY,
        username VARCHAR(30) NOT NULL UNIQUE,
        email VARCHAR(255) NOT NULL UNIQUE,
        level INTEGER DEFAULT 1,
        total_points INTEGER DEFAULT 0
    );
    
    CREATE TABLE friendships (
        id BIGSERIAL PRIMARY KEY,
        player_id BIGINT NOT NULL,
        friend_id BIGINT NOT NULL,
        CONSTRAINT fk_player FOREIGN KEY(player_id) REFERENCES players(id) ON DELETE CASCADE,
        CONSTRAINT fk_friend FOREIGN KEY(friend_id) REFERENCES players(id) ON DELETE CASCADE
    );
    
    CREATE TABLE games (
        id BIGSERIAL PRIMARY KEY,
        datetime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        game_type VARCHAR(255) CHECK (game_type IN ('CLASSIC', 'RANKED', 'CASUAL', 'TOURNAMENT')),
        max_score INTEGER DEFAULT 0,
        host_id BIGINT NOT NULL REFERENCES players(id)
    );
    
    CREATE TABLE participations (
        id BIGSERIAL PRIMARY KEY,
        game_id BIGINT NOT NULL,
        player_id BIGINT NOT NULL,
        score INTEGER NULL,
        victory BOOLEAN NULL,
        CONSTRAINT fk_game FOREIGN KEY(game_id) REFERENCES games(id) ON DELETE CASCADE,
        CONSTRAINT fk_player FOREIGN KEY(player_id) REFERENCES players(id) ON DELETE CASCADE,
        CONSTRAINT unique_participation UNIQUE(game_id, player_id)
    );
    
    CREATE INDEX idx_players_username ON players(username);
    CREATE INDEX idx_players_email ON players(email);
    CREATE INDEX idx_friendships_player_friend ON friendships(player_id, friend_id);
    CREATE INDEX idx_games_host ON games(host_id);
    CREATE INDEX idx_participations_game ON participations(game_id);
    CREATE INDEX idx_participations_player ON participations(player_id);
    ```

#### Verify Installation

Check tables:

```postgresql
-- First, connect to database 
\c ludonexusdb

-- then
\dt
\di
```

To reset everything and start fresh:

```sql
-- As postgres user
\c postgres

DROP DATABASE IF EXISTS ludonexusdb;
DROP USER IF EXISTS lunedev;
```

### Application Configuration

#### PlayerSphere (application.properties)

```properties
# Application Name
spring.application.name=PlayerSphere

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/ludonexusdb
spring.datasource.username=lunedev
spring.datasource.password=lunedev

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Server Configuration
server.port=8080
```

#### BattleForge (application.properties)

```properties
# Application Name
spring.application.name=BattleForge

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/ludonexusdb
spring.datasource.username=lunedev
spring.datasource.password=lunedev

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Server Configuration
server.port=8081
```

#### Test Configuration

Create `src/test/resources/application.properties`:

```properties
# Application Name (same as main)
spring.application.name=PlayerSphere  # or BattleForge

# Test Database Configuration (same database, different behavior)
spring.datasource.url=jdbc:postgresql://localhost:5432/ludonexusdb_test
spring.datasource.username=lunedev
spring.datasource.password=lunedev

# JPA/Hibernate Test Configuration
spring.jpa.hibernate.ddl-auto=create-drop  # Reset database for each test
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Server Configuration (same as main)
server.port=8080  # or 8081 for BattleForge
```

> Note: The test configuration file overrides the main application.properties during test execution. The key difference is `ddl-auto=create-drop` which ensures a clean database state for each test.

### Running the Applications

```bash
# Start PostgreSQL (if not running)
sudo service postgresql start

# Run PlayerSphere
mvn spring-boot:run

# Run BattleForge (in another terminal)
mvn spring-boot:run
```

### Health Check

Verify the services are running:

```bash
# PlayerSphere
curl http://localhost:8080/health

# BattleForge
curl http://localhost:8081/health
```

## Database Structure

LudoNexus uses a single unified PostgreSQL database (ludonexusdb) with logical separation of concerns: 

- Player Management Tables: Store player profiles and social relationships
- Game Management Tables: Contains game sessions and participation data

```mermaid
erDiagram
    %% Player-Friendship Relationships (Bidirectional)
    PLAYERS ||--o{ FRIENDSHIPS : "has friendship as player"
    PLAYERS ||--o{ FRIENDSHIPS : "has friendship as friend"
    
    %% Game-Participation Relationship
    GAMES ||--o{ PARTICIPATIONS : "has"
    
    %% Player-Games (host) Relationship
    PLAYERS ||--o{ GAMES : "hosts"
    
    %% Player-Participation Relationship
    PLAYERS ||--o{ PARTICIPATIONS : "participates in"
    
    PLAYERS {
        bigint id PK
        varchar(30) username UK "not null"
        varchar(255) email UK "not null"
        integer level "default 1"
        integer total_points "default 0"
    }
    
    FRIENDSHIPS {
        bigint id PK
        bigint player_id FK "not null (ref PLAYERS)"
        bigint friend_id FK "not null (ref PLAYERS)"
    }
    
    GAMES {
        bigint id PK
        timestamp datetime "not null"
        varchar(255) game_type "not null"
        integer max_score "default 0"
        bigint host_id FK "not null (ref PLAYERS)"
    }
    
    PARTICIPATIONS {
        bigint id PK
        bigint game_id FK "not null (ref GAMES)"
        bigint player_id FK "not null (ref PLAYERS)"
        integer score "nullable"
        boolean victory "nullable"
    }  
```

### Technical Implementation

#### Service Data Independence

- Each service uses different JPA mapping strategies that reflect their service boundaries:
   - PlayerSphere uses complete JPA entity relationships (`@ManyToOne`, `@OneToMany`) as it owns and manages player data
   - BattleForge uses simple ID references for player relationships to maintain service independence while relying on database foreign keys for integrity

#### Performance Considerations

- Strategic field indexing
- Optimized composite keys
- Appropriate data type selection

#### Data Integrity Rules

- Cascading delete operations
- Business key uniqueness
- Mandatory field constraints

#### Object-Relational Mapping

```mermaid
classDiagram
    %% PlayerSphere Service
    class Player {
        +Long id
        +String username
        +String email
        +Integer level
        +Integer totalPoints
        +List~Friendship~ friendships
    }
    class Friendship {
        +Long id
        +Player player
        +Player friend
    }
    
    %% BattleForge Service
    class Game {
        +Long id
        +LocalDateTime datetime
        +String gameType
        +Integer maxScore
        +Long hostId
        +List~Participation~ participations
        +updateMaxScore()
    }
    class Participation {
        +Long id
        +Game game
        +Long playerId
        +Integer score
        +Boolean victory
    }

    %% Internal Relations
    Player "*" --* "*" Friendship : has
    Game "1" --* "*" Participation : has

    %% Cross-Service Relations
    Player .. Participation : playerId references
    Player .. Game : hostId references
```

## Architecture

### Layered Architecture
Each service (PlayerSphere and BattleForge) follows a strict layered architecture where each layer has specific responsibilities and communicates only with adjacent layers.

```mermaid
flowchart TB
    subgraph External
        Client([Client e.g. Postman])
        OtherService([Other Service])
    end

    subgraph API Layer
        direction TB
        Controller[REST Controllers]
        note1[Request/Response DTOs]
    end

    subgraph Business Layer
        direction TB
        Service[Services]
        note2[Internal DTOs]
    end

    subgraph Data Layer
        direction TB
        Repository[Repositories]
        note3[Entities]
    end

    Database[(PostgreSQL)]

    Client --> Controller
    OtherService --> Controller
    Controller --> Service
    Service --> Repository
    Repository --> Database

    classDef noteStyle fill:#f9f,stroke:#333,stroke-width:1px
    class note1,note2,note3 noteStyle

    classDef layerStyle fill:#def,stroke:#88a,stroke-width:2px
    class Controller,Service,Repository layerStyle

    classDef externalStyle fill:#fdc,stroke:#d88,stroke-width:2px
    class Client,OtherService externalStyle
```

#### Controller Layer
- Exposes REST endpoints
- Handles request validation using `@Valid`
- Manages HTTP status codes via `ResponseEntity`
- Maps request/response DTOs
- No business logic

#### Service Layer
- Manages transactions (`@Transactional`)
- Enforces business rules
- Handles entity/DTO transformations
- Orchestrates repository operations
- Implements validation logic

#### Repository Layer
- Extends `ListCrudRepository`
- Implements custom queries
- Handles pure data operations
- No business logic

#### Data Models
- Request DTOs: API input validation

- Internal DTOs: System-to-system communication and responses

- Entities: Database models

  > Note: Max score calculation is implemented in the Game entity rather than the service layer because:
  >
  > - It requires no external service calls
  > - It depends only on the entity's own data
  > - It's an invariant rule that doesn't change with context
  > - The validation remains valid regardless of which part of the system modifies the entity

### Data Flows

#### Create a player (POST /api/players) 

Creates a new player profile with unique username and email. Initial level is set to 1 and total points to 0.

```mermaid
sequenceDiagram
    participant C as Client
    participant PC as PlayerController
    participant PS as PlayerService
    participant PR as PlayerRepository
    participant DB as Database
    
    C->>PC: POST /api/players {CreatePlayerRequestDTO}
    Note over PC: Validates:<br/>1. Username not null<br/>2. Email format valid
    
    alt Invalid Input
        PC-->>C: 400 Bad Request<br/>{field errors}
    end
    
    PC->>PS: createPlayer(createDTO)
    
    PS->>PR: existsByUsername()
    PR->>DB: SELECT
    DB-->>PR: boolean
    
    alt Username Exists
        PR-->>PS: true
        PS-->>PC: IllegalArgumentException
        PC-->>C: 400 Bad Request<br/>"Username exists"
    end
    
    PS->>PR: existsByEmail()
    PR->>DB: SELECT
    DB-->>PR: boolean
    
    alt Email Exists
        PR-->>PS: true
        PS-->>PC: IllegalArgumentException
        PC-->>C: 400 Bad Request<br/>"Email exists"
    end
    
    Note over PS: 1. Creates Player entity<br/>2. Sets default level=1<br/>3. Sets totalPoints=0
    
    PS->>PR: save(player)
    PR->>DB: INSERT
    DB-->>PR: saved player
    
    Note over PS: Converts to DTO<br/>without friend data
    PS-->>PC: PlayerDTO
    PC-->>C: 200 OK {PlayerDTO}
```

#### Get player profile (GET /api/players/{id}) 

Retrieves player information including statistics and friend list.

```mermaid
sequenceDiagram
    participant C as Client
    participant PC as PlayerController
    participant PS as PlayerService
    participant PR as PlayerRepository
    participant FR as FriendshipRepository
    participant DB as Database
    
    C->>PC: GET /api/players/{id}
    Note over PC: Validates path parameter<br/>is a valid Long
    
    alt Invalid ID Format
        PC-->>C: 400 Bad Request
    end
    
    PC->>PS: getPlayerById(id)
    PS->>PR: findById(id)
    PR->>DB: SELECT player
    DB-->>PR: player data
    
    alt Player Not Found
        PR-->>PS: Optional.empty()
        PS-->>PC: IllegalArgumentException
        PC-->>C: 404 Not Found
    end
    
    Note over PS: 1. Convert player to DTO<br/>2. Load friends list<br/>3. Map friend data
    
    PS-->>PC: PlayerDTO
    PC-->>C: 200 OK {PlayerDTO}
```

#### Get all players (GET /api/players) 

Retrieves a list of all players with their basic information and friend lists.

```mermaid
sequenceDiagram
    participant C as Client
    participant PC as PlayerController
    participant PS as PlayerService
    participant PR as PlayerRepository
    participant DB as Database
    
    C->>PC: GET /api/players
    PC->>PS: getAllPlayers()
    PS->>PR: findAll()
    PR->>DB: SELECT all players
    DB-->>PR: player list
    
    Note over PS: For each player:<br/>1. Convert to DTO<br/>2. Load friend data
    
    alt Database Error
        PR-->>PS: DataAccessException
        PS-->>PC: RuntimeException
        PC-->>C: 500 Internal Server Error
    end
    
    PS-->>PC: List<PlayerDTO>
    PC-->>C: 200 OK [{PlayerDTO}]
```

#### Update player profile (PUT /api/players/{id}) 

Updates player information while ensuring username/email uniqueness is maintained.

```mermaid
sequenceDiagram
    participant C as Client
    participant PC as PlayerController
    participant PS as PlayerService
    participant PR as PlayerRepository
    participant DB as Database
    
    C->>PC: PUT /api/players/{id}<br/>{UpdatePlayerRequestDTO}
    Note over PC: Validates:<br/>1. Path parameter<br/>2. Required fields
    
    alt Invalid Input
        PC-->>C: 400 Bad Request<br/>{field errors}
    end
    
    PC->>PS: updatePlayer(id, updateDTO)
    PS->>PR: findById(id)
    PR->>DB: SELECT
    DB-->>PR: player
    
    alt Player Not Found
        PR-->>PS: Optional.empty()
        PS-->>PC: IllegalArgumentException
        PC-->>C: 404 Not Found
    end
    
    Note over PS: Check username uniqueness<br/>if username changed
    
    alt Username Changed
        PS->>PR: existsByUsername()
        PR->>DB: SELECT
        DB-->>PR: boolean
        alt Username Taken
            PR-->>PS: true
            PS-->>PC: IllegalArgumentException
            PC-->>C: 409 Conflict
        end
    end
    
    Note over PS: Check email uniqueness<br/>if email changed
    
    alt Email Changed
        PS->>PR: existsByEmail()
        PR->>DB: SELECT
        DB-->>PR: boolean
        alt Email Taken
            PR-->>PS: true
            PS-->>PC: IllegalArgumentException
            PC-->>C: 409 Conflict
        end
    end
    
    Note over PS: 1. Update allowed fields<br/>2. Preserve ID and stats
    PS->>PR: save(player)
    PR->>DB: UPDATE
    DB-->>PR: updated player
    
    PS-->>PC: PlayerDTO
    PC-->>C: 200 OK {PlayerDTO}
```

#### Delete player (DELETE /api/players/{id}) 

Removes player profile and all associated friendships.

```mermaid
sequenceDiagram
    participant C as Client
    participant PC as PlayerController
    participant PS as PlayerService
    participant PR as PlayerRepository
    participant FR as FriendshipRepository
    participant DB as Database
    
    C->>PC: DELETE /api/players/{id}
    Note over PC: Validates player ID format
    
    alt Invalid ID Format
        PC-->>C: 400 Bad Request
    end
    
    PC->>PS: deletePlayer(id)
    
    Note over PS: First remove all<br/>friendship relationships
    PS->>FR: deleteByPlayerIdOrFriendId()
    FR->>DB: DELETE
    DB-->>FR: void
    
    PS->>PR: deleteById(id)
    Note over PR,DB: Cascade delete removes<br/>all player data
    PR->>DB: DELETE CASCADE
    DB-->>PR: void
    
    alt Player Not Found
        PR-->>PS: EmptyResultDataAccessException
        PS-->>PC: IllegalArgumentException
        PC-->>C: 404 Not Found
    end
    
    PS-->>PC: void
    PC-->>C: 204 No Content
```

#### Add friend relationship (POST /api/players/{id}/friends) 

Creates bidirectional friendship between two players, preventing self-friendship.

```mermaid
sequenceDiagram
    participant C as Client
    participant PC as PlayerController
    participant PS as PlayerService
    participant PR as PlayerRepository
    participant FR as FriendshipRepository
    participant DB as Database
    
    C->>PC: POST /players/{id}/friends<br/>{CreateFriendshipRequestDTO}
    Note over PC: Validates:<br/>1. Valid player ID<br/>2. Valid friend ID(s)
    
    alt Invalid Request
        PC-->>C: 400 Bad Request<br/>{validation errors}
    end
    
    alt Single Friend
        PC->>PS: createFriendship(id, friendId)
    else Multiple Friends
        Note over PC: Loops through ids array
        PC->>PS: forEach(createFriendship)
    end
    
    PS->>PR: findById(playerId)
    PR->>DB: SELECT player
    DB-->>PR: player
    
    alt Player Not Found
        PR-->>PS: Optional.empty()
        PS-->>PC: IllegalArgumentException
        PC-->>C: 404 Not Found
    end
    
    PS->>PR: findById(friendId)
    PR->>DB: SELECT friend
    DB-->>PR: friend
    
    alt Friend Not Found
        PR-->>PS: Optional.empty()
        PS-->>PC: IllegalArgumentException
        PC-->>C: 404 Not Found
    end
    
    alt Self Friendship Check
        Note over PS: Verify player != friend
        PS-->>PC: IllegalArgumentException
        PC-->>C: 400 Bad Request<br/>"Self friendship"
    end
    
    PS->>FR: existsByPlayerIdAndFriendId()
    FR->>DB: SELECT
    DB-->>FR: boolean
    
    alt Already Friends
        FR-->>PS: true
        PS-->>PC: IllegalArgumentException
        PC-->>C: 409 Conflict
    end
    
    Note over PS: Creates bidirectional<br/>friendship entries
    
    PS->>FR: save(friendship1)
    FR->>DB: INSERT
    DB-->>FR: saved
    
    PS->>FR: save(friendship2)
    FR->>DB: INSERT
    DB-->>FR: saved
    
    PS-->>PC: void
    PC-->>C: 204 No Content
```

#### Get player's friends (GET /api/players/{id}/friends)

Retrieves the list of friends for a specific player.

```mermaid
sequenceDiagram
    participant C as Client
    participant PC as PlayerController
    participant PS as PlayerService
    participant PR as PlayerRepository
    participant FR as FriendshipRepository
    participant DB as Database
    
    C->>PC: GET /api/players/{id}/friends
    Note over PC: Validates path parameter<br/>is a valid Long
    
    alt Invalid ID Format
        PC-->>C: 400 Bad Request
    end
    
    PC->>PS: getPlayerById(id)
    PS->>PR: findById(id)
    PR->>DB: SELECT player
    DB-->>PR: player data
    
    alt Player Not Found
        PR-->>PS: Optional.empty()
        PS-->>PC: IllegalArgumentException
        PC-->>C: 404 Not Found
    end
    
    Note over PS: Extract and convert<br/>friendship data to DTOs
    
    PS-->>PC: List<PlayerFriendDTO>
    PC-->>C: 200 OK [PlayerFriendDTO]
```

#### Remove friend(s) (DELETE /api/players/{id}/friends)

Removes one or multiple friendship relationships. Handles both single friend removal and batch removals.

```mermaid
sequenceDiagram
    participant C as Client
    participant PC as PlayerController
    participant PS as PlayerService
    participant FR as FriendshipRepository
    participant DB as Database
    
    C->>PC: DELETE /api/players/{id}/friends<br/>{CreateFriendshipRequestDTO}
    Note over PC: Validates:<br/>1. Valid player ID<br/>2. Valid request format<br/>3. Either id or ids present
    
    alt Invalid Request
        PC-->>C: 400 Bad Request<br/>{validation errors}
    end
    
    alt Single Friend
        PC->>PS: deleteFriendship(id, friendId)
    else Multiple Friends
        Note over PC: Loops through ids array
        PC->>PS: forEach(deleteFriendship)
    end
    
    Note over PS: Removes both directions<br/>of friendship relation
    
    PS->>FR: deleteByPlayerIdAndFriendId()
    FR->>DB: DELETE friendship1
    DB-->>FR: void
    
    PS->>FR: deleteByPlayerIdAndFriendId()
    FR->>DB: DELETE friendship2
    DB-->>FR: void
    
    alt Database Error
        FR-->>PS: DataAccessException
        PS-->>PC: RuntimeException
        PC-->>C: 500 Internal Server Error
    end
    
    PS-->>PC: void
    PC-->>C: 204 No Content
```

#### Create a game (POST /api/games)

Creates a new game session with required game type and host ID. The service automatically generates a timestamp and creates a participation record for the host player with null score and victory status.

```mermaid
sequenceDiagram
    participant C as Client
    participant GC as GameController
    participant GS as GameService
    participant GR as GameRepository
    participant DB as Database
    
    C->>GC: POST /api/games {GameDTO}
    Note over GC: Validates:<br/>1. gameType not null<br/>2. hostId not null
    
    alt Invalid Input
        GC-->>C: 400 Bad Request<br/>{field errors}
    end
    
    GC->>GS: createGame(GameDTO)
    
    Note over GS: 1. Creates new Game<br/>2. ID auto-generated<br/>3. Date auto-set to now()
    GS->>GS: Instantiation & mapping
    
    Note over GS: Automatically creates host<br/>participation with null<br/>score and victory
    
    GS->>GR: save(game)
    Note over GR,DB: Participations saved<br/>through cascade
    
    alt Database Error
        GR-->>GS: DataAccessException
        GS-->>GC: RuntimeException
        GC-->>C: 500 Internal Server Error
    end
    
    GR->>DB: INSERT
    DB-->>GR: saved game
    
    Note over GS: Converts to DTO using<br/>BeanUtils.copyProperties
    GS-->>GC: GameDTO
    GC-->>C: 200 OK {GameDTO}
```

#### Get a game (GET /api/games/{id}) 

Retrieves detailed game information including all participations. Returns 404 if game not found.

```mermaid
sequenceDiagram
    participant C as Client
    participant GC as GameController
    participant GS as GameService
    participant GR as GameRepository
    participant DB as Database
    
    C->>GC: GET /api/games/{id}
    Note over GC: Validates path parameter<br/>is a valid Long
    
    alt Invalid ID Format
        GC-->>C: 400 Bad Request
    end
    
    GC->>GS: getGameById(id)
    GS->>GR: findById(id)
    GR->>DB: SELECT
    DB-->>GR: game data
    
    alt Game Not Found
        GR-->>GS: Optional.empty()
        GS-->>GC: IllegalArgumentException
        GC-->>C: 404 Not Found
    end
    
    Note over GS: Converts Game and all<br/>participations to DTOs
    GS-->>GC: GameDTO
    GC-->>C: 200 OK {GameDTO}
```

#### Get all games (GET /api/games)

Retrieves a list of all games with their participations. Returns empty list if no games exist.

```mermaid
sequenceDiagram
    participant C as Client
    participant GC as GameController
    participant GS as GameService
    participant GR as GameRepository
    participant DB as Database
    
    C->>GC: GET /api/games
    GC->>GS: getAllGames()
    GS->>GR: findAll()
    GR->>DB: SELECT
    DB-->>GR: all games
    
    Note over GS: For each game:<br/>1. Convert to DTO<br/>2. Map participations
    
    alt Database Error
        GR-->>GS: DataAccessException
        GS-->>GC: RuntimeException
        GC-->>C: 500 Internal Server Error
    end
    
    GS-->>GC: List<GameDTO>
    GC-->>C: 200 OK [{GameDTO}]
```

#### Update a game (PUT /api/games/{id}) 

Updates game information while preserving existing participations. Cannot update game ID or auto-generated fields.

```mermaid
sequenceDiagram
    participant C as Client
    participant GC as GameController
    participant GS as GameService
    participant GR as GameRepository
    participant DB as Database
    
    C->>GC: PUT /api/games/{id} {GameDTO}
    Note over GC: Validates:<br/>1. Path parameter format<br/>2. DTO fields
    
    alt Invalid Input
        GC-->>C: 400 Bad Request<br/>{field errors}
    end
    
    GC->>GS: updateGame(id, GameDTO)
    GS->>GR: findById(id)
    GR->>DB: SELECT
    DB-->>GR: game data
    
    alt Game Not Found
        GR-->>GS: Optional.empty()
        GS-->>GC: IllegalArgumentException
        GC-->>C: 404 Not Found
    end
    
    Note over GS: 1. Update mutable fields<br/>2. Preserve ID & timestamp<br/>3. Keep participations
    
    GS->>GR: save(game)
    GR->>DB: UPDATE
    DB-->>GR: updated game
    
    GS-->>GC: GameDTO
    GC-->>C: 200 OK {GameDTO}
```

#### Delete game (DELETE /api/games/{id}) 

Deletes a game and all its participations through cascade.

```mermaid
sequenceDiagram
    participant C as Client
    participant GC as GameController
    participant GS as GameService
    participant GR as GameRepository
    participant DB as Database
    
    C->>GC: DELETE /games/{id}
    Note over GC: Validates game ID format
    
    alt Invalid ID Format
        GC-->>C: 400 Bad Request
    end
    
    GC->>GS: deleteGame(id)
    
    GS->>GR: deleteById(id)
    Note over GR,DB: Cascade delete removes<br/>all participations
    GR->>DB: DELETE CASCADE
    
    alt Game Not Found
        GR-->>GS: EmptyResultDataAccessException
        GS-->>GC: IllegalArgumentException
        GC-->>C: 404 Not Found
    end
    
    GS-->>GC: void
    GC-->>C: 204 No Content
```

#### Add participation(s) (POST /api/games/{id}/participations) 

Adds one or multiple players to a game. Players cannot participate twice in the same game.

```mermaid
sequenceDiagram
    participant C as Client
    participant GC as GameController
    participant GS as GameService
    participant GR as GameRepository
    participant PR as ParticipationRepository
    participant DB as Database
    
    C->>GC: POST /games/{id}/participations<br/>{IdRequestDTO}
    Note over GC: Validates:<br/>1. Game ID format<br/>2. Either id or ids present<br/>3. ids not empty if present
    
    alt Invalid Request
        GC-->>C: 400 Bad Request<br/>{validation errors}
    end
    
    alt Single Player
        GC->>GS: createParticipation(gameId, playerId)
    else Multiple Players
        Note over GC: Loops through ids array
        GC->>GS: forEach(createParticipation)
    end
    
    GS->>GR: getGameById(gameId)
    GR->>DB: SELECT game
    
    alt Game Not Found
        GR-->>GS: Optional.empty()
        GS-->>GC: IllegalArgumentException
        GC-->>C: 404 Not Found
    end
    
    GS->>PR: existsByGameIdAndPlayerId()
    
    alt Already Participating
        PR-->>GS: true
        GS-->>GC: IllegalArgumentException
        GC-->>C: 409 Conflict
    end
    
    Note over GS: Creates participation<br/>with null score/victory
    GS->>PR: save(participation)
    PR->>DB: INSERT
    
    GS-->>GC: void
    GC-->>C: 204 No Content
```

#### Update participation (PUT /api/games/{id}/participations) 

Updates a player's score and victory status, triggering PlayerSphere statistics update.

```mermaid
sequenceDiagram
    participant C as Client
    participant GC as GameController
    participant GS as GameService
    participant PS as PlayerSphereClient
    participant GR as GameRepository
    participant PR as ParticipationRepository
    participant DB as Database
    
    C->>GC: PUT /games/{id}/participations<br/>{UpdateParticipationDTO}
    Note over GC: Validates:<br/>1. Game ID format<br/>2. Player ID not null<br/>3. Score not null<br/>4. Victory not null
    
    alt Invalid Request
        GC-->>C: 400 Bad Request<br/>{validation errors}
    end
    
    GC->>GS: updateParticipation(id, dto)
    GS->>GR: getGameById(id)
    GR->>DB: SELECT game
    
    alt Game Not Found
        GR-->>GS: Optional.empty()
        GS-->>GC: IllegalArgumentException
        GC-->>C: 404 Not Found
    end
    
    GS->>PR: getByGameIdAndPlayerId()
    PR->>DB: SELECT participation
    
    alt Participation Not Found
        PR-->>GS: Optional.empty()
        GS-->>GC: IllegalArgumentException
        GC-->>C: 404 Not Found
    end
    
    Note over GS: 1. Update score/victory<br/>2. Check maxScore update
    
    GS->>PR: save(participation)
    PR->>DB: UPDATE
    
    Note over GS: Prepare player statistics
    GS->>PS: updatePlayerStats()
    
    alt PlayerSphere Error
        PS-->>GS: Exception
        Note over GS: Log error but<br/>continue
    end
    
    GS-->>GC: GameParticipationDTO
    GC-->>C: 200 OK {GameParticipationDTO}
```

#### Delete game participations (DELETE /api/games/{id}/participations)

Removes all participations from a game and resets maximum score.

```mermaid
sequenceDiagram
    participant C as Client
    participant GC as GameController
    participant GS as GameService
    participant GR as GameRepository
    participant DB as Database
    
    C->>GC: DELETE /games/{id}/participations
    Note over GC: Validates game ID format
    
    alt Invalid ID Format
        GC-->>C: 400 Bad Request
    end
    
    GC->>GS: removeGameParticipations(id)
    GS->>GR: findById(id)
    GR->>DB: SELECT game
    
    alt Game Not Found
        GR-->>GS: Optional.empty()
        GS-->>GC: IllegalArgumentException
        GC-->>C: 404 Not Found
    end
    
    Note over GS: 1. Clear participations<br/>2. Reset maxScore to 0
    
    GS->>GR: save(game)
    Note over GR,DB: Cascade deletes<br/>all participations
    GR->>DB: DELETE + UPDATE
    
    GS-->>GC: void
    GC-->>C: 204 No Content
```

#### Remove player participations (DELETE /api/games/ofplayer) 

Removes all participations for given player(s) and updates affected games.

```mermaid
sequenceDiagram
    participant C as Client
    participant GC as GameController
    participant GS as GameService
    participant GR as GameRepository
    participant PR as ParticipationRepository
    participant DB as Database
    
    C->>GC: DELETE /games/ofplayer<br/>{IdRequestDTO}
    Note over GC: Validates:<br/>1. Either id or ids present<br/>2. ids not empty if present
    
    alt Invalid Request
        GC-->>C: 400 Bad Request<br/>{validation errors}
    end
    
    alt Single Player
        GC->>GS: removePlayerParticipations(id)
    else Multiple Players
        Note over GC: Loops through ids array
        GC->>GS: forEach(removePlayerParticipations)
    end
    
    GS->>GR: findAllByHostId()
    GR->>DB: SELECT hosted games
    
    Note over GS: For each hosted game:<br/>1. Clear host ID<br/>2. Update game
    
    GS->>PR: deleteByPlayerId()
    PR->>DB: DELETE participations
    
    GS-->>GC: void
    GC-->>C: 204 No Content
```

### Testing Strategy

1. Controller Layer Tests
   - API contract validation
   - Input validation
   - HTTP status code handling
2. Service Layer Tests
   - Business logic unit tests
   - Transaction management
   - Integration tests
3. Repository Layer Tests
   - Query functionality
   - Database interactions
