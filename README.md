# ID Card Manager System

A Spring Boot web application for managing digital ID cards with dynamic templates, QR code generation, and barcode support.

## Features

✨ **Core Capabilities**
- Create and manage digital ID card profiles
- Dynamic template system with customizable colors (Hex colors for HTML and PDF)
- Auto-generate unique registration numbers with custom formats
- Store profile photos as local files
- Generate QR codes and barcodes (CODE_128, EAN_13)
- Live HTML preview with real-time styling
- Search and filter profiles by name
- Complete CRUD operations for profiles and templates

🎨 **Template System**
- Pre-configured templates: Professional Blue, Corporate Red, Modern Green
- Customizable organization name, tagline, and branding colors
- Support for vertical and horizontal layouts
- Dynamic styling applied to both HTML preview and PDF export

📋 **Profile Management**
- Support for multiple profile types: Student, Employee, User
- Rich profile data: Name, Department, Title, Email, Phone, Blood Group, Date of Birth
- Automatic UUID generation for unique identification
- Timestamps for creation and updates
- Photo upload with content-type validation (JPEG, PNG)

🔐 **Data Persistence**
- MySQL database with JPA/Hibernate ORM
- Unique constraints on registration numbers
- Audit timestamps (createdAt, updatedAt)
- Relationships between Profiles and Templates

## Tech Stack

- **Framework**: Spring Boot 3.5.15
- **Language**: Java 17+
- **Database**: MySQL 8.0+
- **Template Engine**: Thymeleaf
- **ORM**: Spring Data JPA / Hibernate
- **Barcode Generation**: ZXing 3.5.3
- **PDF Generation**: OpenPDF 1.3.30
- **Build Tool**: Maven
- **Testing**: JUnit 5

## Project Structure

```
src/main/java/com/example/idcardmanager/
├── model/
│   ├── Profile.java              # Main profile entity
│   ├── Template.java             # Card template entity
│   ├── ProfileBuilder.java       # Builder utility for profiles
│   ├── ProfileType.java          # Enum: STUDENT, EMPLOYEE, USER
│   └── BarcodeType.java          # Enum: CODE_128, EAN_13
├── repository/
│   ├── ProfileRepository.java    # Profile JPA repository
│   └── TemplateRepository.java   # Template JPA repository
├── service/
│   ├── ProfileService.java       # Profile business logic & file handling
│   ├── TemplateService.java      # Template management
│   └── AssetGenerationService.java # QR/Barcode generation
├── controller/
│   └── ProfileWebController.java # Web endpoints and views
└── DemoApplication.java          # Spring Boot main class

src/main/resources/
├── application.properties        # Configuration
└── templates/
    ├── index.html                # Profile creation form
    ├── card_preview_fragment.html # Live card preview
    ├── profiles_list.html        # Profile listing & search
    └── error.html                # Error page

src/test/java/com/example/idcardmanager/
└── ProfileAndTemplateTest.java  # Comprehensive integration tests
```

## Setup Instructions

### 1. Prerequisites

- Java 17+
- MySQL 8.0+
- Maven 3.6+

### 2. Database Configuration

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/id_card_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
```

### 3. Build & Run

```bash
# Build the project
./mvnw clean install

# Run the application
./mvnw spring-boot:run

# Application will be available at http://localhost:8080
```

### 4. Run Tests

```bash
./mvnw test
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/profiles` | Show profile creation form |
| POST | `/profiles/preview` | Generate live card preview |
| POST | `/profiles/save` | Save profile to database |
| GET | `/profiles/{uuid}` | View specific profile card |
| GET | `/profiles/list` | List all profiles |
| GET | `/profiles/search` | Search profiles by name |
| POST | `/profiles/{uuid}/delete` | Delete a profile |

## Usage Example

1. **Create a Profile**
   - Navigate to `http://localhost:8080/profiles`
   - Fill in personal and professional information
   - Upload a photo (optional)
   - Select a template
   - Click "Generate Preview"

2. **Preview the Card**
   - View the live card preview with dynamic colors
   - QR code and barcode are auto-generated
   - Print the card using browser print function

3. **Save Profile**
   - After preview, save the profile to database
   - Profile is assigned UUID and auto-generated registration number
   - Photo is stored locally in `uploads/photos/` directory

4. **Manage Profiles**
   - Browse all profiles from `/profiles/list`
   - Search profiles by name
   - Delete profiles as needed

## Database Schema

### Profiles Table
```sql
CREATE TABLE profiles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    uuid VARCHAR(36) UNIQUE NOT NULL,
    registration_number VARCHAR(64) UNIQUE NOT NULL,
    type VARCHAR(16) NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    department VARCHAR(80),
    title VARCHAR(120),
    email VARCHAR(120),
    phone VARCHAR(40),
    blood_group VARCHAR(60),
    date_of_birth DATE,
    issue_date DATE,
    expiry_date DATE,
    photo_file_name VARCHAR(255),
    photo_content_type VARCHAR(60),
    template_id BIGINT,
    barcode_type VARCHAR(16),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (template_id) REFERENCES templates(id)
);
```

### Templates Table
```sql
CREATE TABLE templates (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(60) UNIQUE NOT NULL,
    name VARCHAR(80) NOT NULL,
    organization_name VARCHAR(120),
    layout VARCHAR(20) NOT NULL,
    primary_color VARCHAR(7) NOT NULL,
    secondary_color VARCHAR(7) NOT NULL,
    text_color VARCHAR(7) NOT NULL,
    tagline VARCHAR(255)
);
```

## File Upload Configuration

- **Location**: `uploads/photos/` (relative to application)
- **Max File Size**: 5MB
- **Supported Formats**: JPEG, PNG
- **Naming**: `UUID_originalfilename.ext`

## Default Templates

The application auto-creates three default templates on startup:

1. **Professional Blue**
   - Primary: #1d4ed8 (Blue)
   - Secondary: #e0e7ff (Light Blue)
   - Text: #111827 (Dark Gray)

2. **Corporate Red**
   - Primary: #dc2626 (Red)
   - Secondary: #fee2e2 (Light Red)
   - Text: #111827 (Dark Gray)

3. **Modern Green**
   - Primary: #059669 (Green)
   - Secondary: #d1fae5 (Light Green)
   - Text: #111827 (Dark Gray)

## Testing

The project includes comprehensive unit tests covering:
- Template CRUD operations
- Profile creation and retrieval
- Uniqueness constraints
- Search functionality
- File upload validation
- Barcode and QR code generation
- Audit timestamp validation

Run tests with:
```bash
./mvnw test
```

## Git Repository

Initial commit includes:
- Complete model layer with JPA entities
- Repository interfaces with custom queries
- Service layer with business logic
- Web controller with all endpoints
- Thymeleaf templates for UI
- Comprehensive unit tests
- Maven build configuration

Initialize remote repository:
```bash
git remote add origin https://github.com/YOUR_USERNAME/id-card-manager.git
git branch -M main
git push -u origin main
```

## Future Enhancements

- [ ] PDF export functionality
- [ ] Email-based profile sharing
- [ ] Batch profile import/export (CSV, Excel)
- [ ] Multi-language support
- [ ] Role-based access control
- [ ] API authentication (JWT tokens)
- [ ] Mobile-responsive improvements
- [ ] Dark mode support
- [ ] Advanced search filters
- [ ] Profile expiry notifications

## Troubleshooting

### MySQL Connection Issues
- Ensure MySQL service is running
- Check database URL and credentials in `application.properties`
- Create database: `CREATE DATABASE id_card_db;`

### Photo Upload Failures
- Check `uploads/photos/` directory permissions
- Verify file size doesn't exceed 5MB
- Confirm MIME type is image/jpeg or image/png

### Barcode Generation Errors
- EAN-13 requires exactly 13 numeric digits
- CODE_128 supports alphanumeric strings
- Verify input format before generation

## License

This project is part of the CTP Software Engineering course.

## Support

For issues or questions, please check the project repository or contact the development team.
