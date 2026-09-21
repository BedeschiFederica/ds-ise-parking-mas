# Inter-Agent Communication

## AreaAgent - ParkingAgent: initial information collection
```mermaid
sequenceDiagram
    participant A as AreaAgent
    participant P as ParkingAgent

    A->>P: Request parking information
    P-->>A: Position and available spots
    A->>A: Store parking status
```

## DriverAgent - AreaAgent: request parking
```mermaid
sequenceDiagram
    participant D as DriverAgent
    participant A as AreaAgent

    D->>A: Request closest parking lot with at least<br/>a given number of available spots
    A->>A: Find suitable parking lot
    alt Found
        A-->>D: parking name and position
        D->>D: Go to parking lot
    else Not found
        A-->>D: none
        D->>D: Request parking from nearest area agent
    end
```

## DriverAgent - ParkingAgent: parking entry
```mermaid
sequenceDiagram
participant D as DriverAgent D
participant P as ParkingAgent P
participant E as Environment

    D->>P: Request entry into parking lot P
    alt Spot available
        P->>E: enter_driver(D)
        E->>E: Move D into parking lot P
        E-->>P: true
        P->>P: Decrement available spots
        P-->>D: granted
        D->>D: Stay in parking lot P, then exit
    else No spot available
        P-->>D: denied
        D->>D: Request another parking from AreaAgent
    end
```

## DriverAgent - ParkingAgent: parking exit
```mermaid
sequenceDiagram
participant D as DriverAgent D
participant P as ParkingAgent P
participant E as Environment

    D->>P: Request exit from parking lot P
    P->>E: exit_driver(D, Direction)
    E->>E: Move D out of parking lot P<br/>in direction Direction
    E-->>P: success
    P->>P: Increment available spots
    P-->>D: exit confirmed
    D->>D: Go home
```

## ParkingAgent - AreaAgent: parking availability updates
```mermaid
sequenceDiagram
participant P as ParkingAgent P
participant A as AreaAgent

    alt Driver enters parking lot P
        P->>P: Decrement available spots
    else Driver exits parking lot P
        P->>P: Increment available spots
    end
    P->>A: Update parking lot P with new availability
    A->>A: Update parking lot P status
```