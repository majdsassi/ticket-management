# 🎫 Ticket Support System

A full-featured, production-ready ticket management system built with Spring Boot, Spring Security, and Tailwind CSS. Perfect for managing customer support requests with role-based access control.

## 🚀 Features

### Core Functionality
- **Ticket Management**: Create, view, edit, and manage support tickets
- **Role-Based Access Control**: Admin, Support Agent, and Customer roles
- **Project Organization**: Organize tickets by projects
- **Priority & Status Tracking**: URGENT, HIGH, MEDIUM, LOW priorities and OPEN, ASSIGNED, RESOLVED, CLOSED statuses
- **User Management**: User registration and authentication with password hashing
- **Dashboard Analytics**: Real-time dashboard with key metrics for each role

### User Roles

#### 👤 Customer
- Create new support tickets
- View and update their own tickets
- Track ticket status and progress
- Add comments to tickets

#### 🛠️ Support Agent (Customer Support)
- View all pending tickets
- Assign tickets to themselves
- Update ticket status and priority
- Manage customer tickets
- View support queue with urgent items first

#### 👨‍💼 Admin
- Full system access
- Manage all users and their roles
- Manage projects
- View system statistics and analytics
- Delete tickets if needed
- Access all dashboards

### Design & UI
- **Tailwind CSS**: Modern, responsive design
- **Kinetic Ops Design System**: Professional, clean aesthetic
- **Role-Based Dashboards**: Customized views for each role
- **Real-Time Data**: Dynamic content updates
- **Mobile Responsive**: Works on desktop and mobile devices

## 🛠️ Technology Stack

- **Backend**: Spring Boot 4.0.5
- **Security**: Spring Security with BCrypt password encoding
- **Database**: MariaDB/MySQL
- **ORM**: JPA/Hibernate
- **Frontend**: Thymeleaf + Tailwind CSS
- **Build**: Maven
- **Java Version**: 17+

## 📋 Prerequisites

- JDK 17 or higher
- MariaDB/MySQL 5.7+
- Maven 3.6+
- Git

## 🔧 Installation & Setup

### 1. Clone or Download the Project

```bash
cd "D:\ISETSO\S4\Advanced Java\PROJECT"
```

### 2. Configure Database

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/TICKET?...
spring.datasource.username=root
spring.datasource.password=root
```

Make sure MariaDB is running and the `TICKET` database exists (it will be auto-created if `createDatabaseIfNotExist=true`).

### 3. Build the Project

```bash
mvn clean package
```

**Note**: Requires a JDK (not just JRE) for compilation.

### 4. Run the Application

```bash
mvn spring-boot:run
```

Or run the built JAR:

```bash
java -jar target/PROJECT-0.0.1-SNAPSHOT.jar
```

The application will start at **http://localhost:8089**

## 📝 Default Test Credentials

The system creates default users automatically on startup. Use these to test:

### Admin Account
- **Email**: admin@example.com
- **Password**: admin123
- **Role**: Administrator

### Support Agents
- **Email**: support1@example.com
- **Password**: support123
- **Email**: support2@example.com
- **Password**: support123

### Customers
- **Email**: customer1@example.com
- **Password**: customer123
- **Email**: customer2@example.com
- **Password**: customer123

## 🎯 Usage Guide

### For Customers
1. Register at `/register` or login with customer credentials
2. Click "Open New Ticket" to create a support request
3. Fill in the title, description, project, and priority
4. View your tickets in "My Tickets" dashboard
5. Track status and view updates

### For Support Agents
1. Login with support agent credentials
2. Dashboard shows pending tickets and your assignments
3. View "Support Queue" for unassigned tickets
4. Click on tickets to assign and update status
5. Change priority and mark as resolved when complete

### For Admins
1. Login with admin credentials
2. Access "Admin Dashboard" for system overview
3. Manage Users: View all users, their roles, and status
4. Manage Projects: Create and organize projects
5. View Statistics: See ticket distribution and system health
6. Full control over all tickets and users

## 📂 Project Structure

```
src/main/
├── java/com/example/project/
│   ├── controller/          # MVC Controllers
│   ├── model/              # JPA Entities
│   ├── repository/         # Data Access Layer
│   ├── service/            # Business Logic
│   ├── dto/                # Data Transfer Objects
│   ├── config/             # Configuration Classes
│   └── ProjectApplication.java
└── resources/
    ├── application.properties
    └── templates/          # Thymeleaf HTML Templates
        ├── auth/           # Login/Register pages
        ├── dashboard/      # Role-specific dashboards
        └── tickets/        # Ticket management pages
```

## 🔑 Key Components

### Security Configuration (`SecurityConfig.java`)
- Spring Security setup with BCrypt password encoding
- Role-based authorization rules
- Login/logout configuration
- CSRF protection

### User Service (`UserService.java`)
- User registration and validation
- Password management
- Role assignment

### Ticket Service (`TicketService.java`)
- Ticket lifecycle management
- Assignment and status tracking
- Permission checking

### Data Initializer (`DataInitializer.java`)
- Automatic creation of test users and projects
- Runs on application startup

## 🎨 UI Features

- **Modern Navbar**: Navigation with user info and logout
- **Sidebar Menu**: Quick access to main features
- **Status Badges**: Color-coded ticket statuses
- **Priority Indicators**: Visual priority levels
- **Responsive Tables**: Sortable, filterable ticket lists
- **Form Validation**: Client-side and server-side validation
- **Alert Messages**: Success and error notifications

## 🔐 Security Features

- ✅ Password hashing with BCrypt
- ✅ Role-based access control (RBAC)
- ✅ CSRF protection
- ✅ Session management
- ✅ Secure password reset capability
- ✅ Active user status tracking

## 🐛 Troubleshooting

### "No compiler is provided" error
**Solution**: Make sure you have a JDK installed, not just a JRE. Download JDK 17+ from oracle.com

### Database connection failed
**Solution**: Ensure MariaDB/MySQL is running on localhost:3306. Check your credentials in `application.properties`

### Port 8089 already in use
**Solution**: Change the port in `application.properties`:
```properties
server.port=8090
```

### No tables created
**Solution**: Check that `spring.jpa.hibernate.ddl-auto=update` is set in `application.properties`

## 📚 API Endpoints

### Authentication
- `GET /login` - Login page
- `POST /login` - Process login
- `GET /register` - Registration page
- `POST /register` - Create new user
- `POST /logout` - Logout

### Tickets
- `GET /tickets` - List all tickets (role-based)
- `GET /tickets/new` - Create ticket form
- `POST /tickets` - Create new ticket
- `GET /tickets/{id}` - View ticket details
- `POST /tickets/{id}/update` - Update ticket
- `POST /tickets/{id}/delete` - Delete ticket

### Dashboard
- `GET /dashboard` - Role-based dashboard

## 📊 Database Schema

### Users Table
- `id`: Primary key
- `full_name`: User's full name
- `email`: Unique email (username)
- `password`: Bcrypt hashed password
- `role`: Enum (ADMIN, CUSTOMER_SUPPORT, CUSTOMER)
- `active`: Boolean status
- `created_at`: Timestamp
- `updated_at`: Timestamp

### Tickets Table
- `id`: Primary key
- `title`: Ticket subject
- `description`: Detailed issue description
- `status`: Enum (OPEN, ASSIGNED, RESOLVED, CLOSED)
- `priority`: Enum (URGENT, HIGH, MEDIUM, LOW)
- `project_id`: Foreign key to projects
- `reporter_id`: Foreign key to users (creator)
- `assignee_id`: Foreign key to users (assigned to)
- `created_at`: Timestamp
- `updated_at`: Timestamp

### Comments Table
- `id`: Primary key
- `content`: Comment text
- `ticket_id`: Foreign key
- `author_id`: Foreign key to users
- `created_at`: Timestamp

### Projects Table
- `id`: Primary key
- `name`: Project name
- `description`: Project description

## 🚀 Deployment

1. Build the application: `mvn clean package`
2. Deploy the JAR to your server
3. Set environment variables or update `application.properties`:
   - Database credentials
   - Server port
   - Security settings
4. Run: `java -jar PROJECT-0.0.1-SNAPSHOT.jar`

## 📝 License & Credits

This is a custom ticket management system built for educational and commercial purposes. Feel free to modify and extend as needed.

## 🤝 Support

For issues or questions:
1. Check the troubleshooting section
2. Review logs in `target/` directory
3. Check database connectivity
4. Verify user credentials and roles

---

**Happy Ticketing! 🎫✨**
