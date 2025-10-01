# E-commerce API

An API for a complete e-commerce platform built using Java and Spring Boot. This project provides the backend functionality to manage products, categories, users, orders, carts, and more.

## Features

*   **Authentication**: User registration and login using JWT.
*   **User & Role Management**: Manage users and their access rights.
*   **Product Management**: Create, read, update, and delete products.
*   **Category Management**: Organize products into categories.
*   **Shopping Cart**: Add, remove, and view items in a shopping cart.
*   **Order Management**: Place orders and view order history.
*   **Merchant Features**: Onboard and manage merchants on the platform.
*   **Product Reviews**: Allow users to review products.
*   **Shipping Addresses**: Manage user shipping addresses.

## Technologies Used

*   **Java 21**: The primary programming language.
*   **Spring Boot**: Framework for building the application.
*   **Spring Security & JWT**: For authentication and authorization.
*   **Spring Data JPA**: For database interaction.
*   **PostgreSQL**: Database management system.
*   **Maven**: Dependency management and build tool.
*   **Springdoc OpenAPI (Swagger)**: For API documentation.

## How to Run the Project

### Prerequisites

*   Java Development Kit (JDK) 21 or higher.
*   Maven.
*   PostgreSQL.

### Installation & Running

1.  **Clone this repository:**
    ```bash
    git clone https://github.com/MamangRust/example-ecommerce-springboot-new
    cd example-ecommerce-springboot-new
    ```

2.  **Database Configuration:**
    Open `src/main/resources/application.properties` and adjust your PostgreSQL database configuration.
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce_db
    spring.datasource.username=postgres
    spring.datasource.password=postgres
    spring.jpa.hibernate.ddl-auto=update
    ```

3.  **Build and run the application using the Maven Wrapper:**
    For Linux/Mac:
    ```bash
    ./mvnw spring-boot:run
    ```
    For Windows:
    ```bash
    mvnw.cmd spring-boot:run
    ```

4.  The application will be running at `http://localhost:8080`.

## API Documentation

Once the application is running, the interactive API documentation (Swagger UI) can be accessed at:
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

Through the Swagger UI, you can see all available endpoints, data models, and try them out directly.

## Entity Relationship Diagram

```mermaid
erDiagram
    USERS {
        Long userId PK
        String username
        String firstname
        String lastname
        String email
        String password
    }

    ROLES {
        Long roleId PK
        String roleName
    }

    USER_ROLES {
        Long user_role_id PK
        Long user_id FK
        Long role_id FK
    }

    REFRESH_TOKENS {
        Long refreshTokenId PK
        Long user_id FK
        String token
    }

    MERCHANTS {
        Long merchantId PK
        Integer userId FK
        String name
        String description
        String address
        Status status
    }

    PRODUCTS {
        Long productId PK
        Integer merchantId FK
        Integer categoryId FK
        String name
        String description
        Integer price
        Integer countInStock
    }

    CATEGORIES {
        Long categoryId PK
        String name
        String description
    }

    CARTS {
        Long cartId PK
        Integer userId FK
        Integer productId FK
        Integer quantity
    }

    ORDERS {
        Long orderId PK
        Integer userId FK
        Integer merchantId FK
        Integer totalPrice
    }

    ORDER_ITEMS {
        Long orderItemId PK
        Integer orderId FK
        Integer productId FK
        Integer quantity
        Integer price
    }

    SHIPPING_ADDRESSES {
        Long shippingAddressId PK
        Integer orderId FK
        String alamat
        String kota
        String negara
    }

    TRANSACTIONS {
        Long transactionId PK
        Integer orderId FK
        Integer merchantId FK
        String paymentMethod
        PaymentStatus status
    }

    REVIEWS {
        Long reviewId PK
        Integer userId FK
        Integer productId FK
        String comment
        Integer rating
    }

    REVIEW_DETAILS {
        Long reviewDetailId PK
        Integer reviewId FK
        String type
        String url
    }

    MERCHANT_DETAILS {
        Long merchantDetailId PK
        Integer merchantId FK
        String displayName
        String logoUrl
    }

    MERCHANT_SOCIAL_MEDIA_LINKS {
        Long merchantSocialId PK
        Integer merchantDetailId FK
        String platform
        String url
    }

    MERCHANT_POLICIES {
        Long merchantPolicyId PK
        Integer merchantId FK
        String policyType
        String title
    }

    MERCHANT_BUSINESS_INFORMATION {
        Long merchantBusinessInfoId PK
        Integer merchantId FK
        String businessType
        String taxId
    }

    MERCHANT_CERTIFICATIONS_AND_AWARDS {
        Long merchantCertificationId PK
        Integer merchantId FK
        String title
        String issuedBy
    }

    BANNERS {
        Long bannerId PK
        String name
        Date startDate
        Date endDate
    }

    SLIDERS {
        Long sliderId PK
        String name
        String image
    }

    USERS ||--|{ REFRESH_TOKENS : "has"
    USERS ||--o{ USER_ROLES : "has"
    ROLES ||--o{ USER_ROLES : "has"
    USERS ||--|{ MERCHANTS : "owns"
    USERS ||--|{ ORDERS : "places"
    USERS ||--|{ CARTS : "has"
    USERS ||--|{ REVIEWS : "writes"
    MERCHANTS ||--|{ PRODUCTS : "sells"
    MERCHANTS ||--|{ ORDERS : "receives"
    MERCHANTS ||--|{ MERCHANT_DETAILS : "has"
    MERCHANTS ||--|{ MERCHANT_POLICIES : "has"
    MERCHANTS ||--|{ MERCHANT_BUSINESS_INFORMATION : "has"
    MERCHANTS ||--|{ MERCHANT_CERTIFICATIONS_AND_AWARDS : "has"
    MERCHANT_DETAILS ||--|{ MERCHANT_SOCIAL_MEDIA_LINKS : "has"
    CATEGORIES ||--|{ PRODUCTS : "contains"
    PRODUCTS ||--o{ CARTS : "in"
    PRODUCTS ||--o{ ORDER_ITEMS : "in"
    PRODUCTS ||--o{ REVIEWS : "has"
    ORDERS ||--|{ ORDER_ITEMS : "contains"
    ORDERS ||--|| SHIPPING_ADDRESSES : "ships to"
    ORDERS ||--|{ TRANSACTIONS : "has"
    REVIEWS ||--|{ REVIEW_DETAILS : "has"

```


### 

<img src="./images/openapi.png" alt="openapi" />