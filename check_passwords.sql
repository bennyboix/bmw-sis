-- Check if passwords are hashed in the database
-- BCrypt hashes should be 60 characters long and start with $2a$, $2b$, etc.

-- Check admin passwords
SELECT 
    admin_id, 
    LEFT(password, 20) as password_preview, 
    LENGTH(password) as password_length,
    CASE 
        WHEN LENGTH(password) = 60 AND password LIKE '$2%$%' THEN 'HASHED'
        ELSE 'PLAIN TEXT'
    END as password_type
FROM admin 
LIMIT 5;

-- Check faculty passwords  
SELECT 
    faculty_id, 
    first_name,
    LEFT(password, 20) as password_preview, 
    LENGTH(password) as password_length,
    CASE 
        WHEN LENGTH(password) = 60 AND password LIKE '$2%$%' THEN 'HASHED'
        ELSE 'PLAIN TEXT'
    END as password_type
FROM faculty 
LIMIT 5;

-- Check student passwords
SELECT 
    student_id, 
    first_name,
    LEFT(password, 20) as password_preview, 
    LENGTH(password) as password_length,
    CASE 
        WHEN LENGTH(password) = 60 AND password LIKE '$2%$%' THEN 'HASHED'
        ELSE 'PLAIN TEXT'
    END as password_type
FROM students 
LIMIT 5; 