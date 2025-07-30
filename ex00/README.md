# 🎬 Cinema Spring Boot - Exercise 00: Spring Security Implementation

## 📋 Table of Contents

1. [Overview](#overview)
2. [Requirements Analysis](#requirements-analysis)
3. [Core Spring Security Concepts](#core-spring-security-concepts)
4. [Implementation Deep Dive](#implementation-deep-dive)
5. [Security Configuration](#security-configuration)
6. [Authentication Flow](#authentication-flow)
7. [CSRF Protection](#csrf-protection)
8. [Remember-Me Functionality](#remember-me-functionality)
9. [Testing & Validation](#testing--validation)
10. [Troubleshooting Guide](#troubleshooting-guide)

## 🎯 Overview

This exercise implements a complete Spring Security authentication system for a cinema application. We built a secure login system with custom user management, role-based access control, CSRF protection, and remember-me functionality.

### Key Features Implemented:

- ✅ Custom UserDetails and UserDetailsService implementations
- ✅ Independent custom /signIn page (no built-in Spring Security pages)
- ✅ Remember-me functionality with persistent tokens
- ✅ CSRF protection against cross-site request forgery attacks
- ✅ User roles stored as Enum values (ADMIN, USER)
- ✅ Role-based access control for admin panels
- ✅ Secure logout with proper session management

## 📝 Requirements Analysis

### Original Requirements:

1. **Spring Security Interfaces**: Implement UserDetails and UserDetailsService
2. **Custom Login Page**: Create independent /signIn page (no built-in pages)
3. **Remember-Me**: Implement persistent login functionality
4. **CSRF Protection**: Ensure protection against CSRF attacks
5. **Role Enum**: Store user roles as Enum values

### Additional Features Added:

- Password encryption with BCrypt
- Role-based URL protection
- Proper logout functionality
- Error handling and user feedback
- Test user accounts for development

## 🔐 Core Spring Security Concepts

### 1. Authentication vs Authorization

- **Authentication**: "Who are you?" - Verifying user identity
- **Authorization**: "What can you do?" - Checking user permissions

### 2. Spring Security Architecture

```
Request → Security Filter Chain → Authentication → Authorization → Controller
```

### 3. Key Components:

- **SecurityFilterChain**: Defines security rules and filters
- **UserDetailsService**: Loads user data during authentication
- **UserDetails**: Represents authenticated user with authorities
- **PasswordEncoder**: Encrypts and validates passwords
- **AuthenticationManager**: Coordinates authentication process

## 🏗️ Implementation Deep Dive

### 1. User Entity & Role Enum

**Role.java** - Simple enum for user roles:

```java
public enum Role {
    ADMIN,  // Full access to admin panels
    USER    // Basic user access
}
```

**User.java** - JPA entity with security fields:

```java
@Entity
@Table(name = "users")
public class User {
    @Enumerated(EnumType.STRING)  // Store as string in DB
    private Role role = Role.USER;

    @Enumerated(EnumType.STRING)
    private Status status = Status.NOT_CONFIRMED;

    // Other fields: username, password, email, etc.
}
```

**Key Points:**

- `@Enumerated(EnumType.STRING)`: Stores enum as readable string in database
- Default role is USER for new accounts
- Status controls account activation (CONFIRMED/NOT_CONFIRMED)

### 2. UserDetails Implementation

**UserPrincipal.java** - Wraps User entity for Spring Security:

```java
public class UserPrincipal implements UserDetails {
    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convert Role enum to Spring Security authority
        return Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
    }

    @Override
    public boolean isEnabled() {
        // Only CONFIRMED users can login
        return user.getStatus() == User.Status.CONFIRMED;
    }
}
```

**Key Points:**

- Spring Security expects authorities with "ROLE\_" prefix
- `isEnabled()` controls account activation
- Other methods (isAccountNonExpired, etc.) return true for simplicity

### 3. UserDetailsService Implementation

**CustomUserDetailsService.java** - Loads users from database:

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new UserPrincipal(user);
    }
}
```

**Key Points:**

- Called automatically during authentication
- Must throw UsernameNotFoundException for invalid users
- Returns UserPrincipal wrapping the User entity

## ⚙️ Security Configuration

### SecurityConfig.java - The Heart of Spring Security

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // URL-based authorization
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/admin/panel/**").hasRole("ADMIN")
                .requestMatchers("/profile").authenticated()
                .requestMatchers("/signIn", "/signUp").permitAll()
                .anyRequest().authenticated()
            )
            // Custom login configuration
            .formLogin(form -> form
                .loginPage("/signIn")
                .loginProcessingUrl("/signIn")
                .defaultSuccessUrl("/profile", true)
                .failureUrl("/signIn?error=true")
            )
            // Remember-me configuration
            .rememberMe(remember -> remember
                .key("cinema-remember-me-key")
                .tokenValiditySeconds(86400) // 24 hours
                .userDetailsService(customUserDetailsService)
            )
            // CSRF enabled by default
            .userDetailsService(customUserDetailsService);

        return http.build();
    }
}
```

### Authorization Rules Breakdown:

1. **Admin-Only URLs**: `/admin/panel/**` requires ADMIN role
2. **Authenticated URLs**: `/profile` requires any authenticated user
3. **Public URLs**: `/signIn`, `/signUp` accessible to everyone
4. **Default Rule**: All other URLs require authentication

### Form Login Configuration:

- **loginPage**: Custom login page URL
- **loginProcessingUrl**: Where form submits (Spring Security handles this)
- **defaultSuccessUrl**: Redirect after successful login
- **failureUrl**: Redirect after failed login

## 🔄 Authentication Flow

### Step-by-Step Process:

1. **User Submits Login Form**:

   ```
   POST /signIn
   username=admin&password=password123&remember-me=on&_csrf=token
   ```

2. **Spring Security Intercepts**:

   - UsernamePasswordAuthenticationFilter processes the request
   - Extracts username, password, and remember-me flag

3. **UserDetailsService Called**:

   ```java
   UserDetails userDetails = customUserDetailsService.loadUserByUsername("admin");
   ```

4. **Password Validation**:

   - BCryptPasswordEncoder compares submitted password with stored hash
   - If match: authentication successful
   - If no match: BadCredentialsException thrown

5. **Authority Assignment**:

   - UserPrincipal.getAuthorities() returns user's roles
   - SecurityContext populated with authenticated user

6. **Remember-Me Processing** (if checked):

   - Creates encrypted token with username + expiration
   - Sets HttpOnly cookie with 24-hour expiration

7. **Redirect to Success URL**:
   - Admin users → `/admin/panel/halls`
   - Regular users → `/profile`

## 🛡️ CSRF Protection

### What is CSRF?

Cross-Site Request Forgery attacks trick users into performing unwanted actions on authenticated sessions.

### How Spring Security Prevents CSRF:

1. **Token Generation**: Each form gets a unique CSRF token
2. **Token Validation**: Server validates token on form submission
3. **Automatic Integration**: Thymeleaf automatically includes tokens

### Implementation in Templates:

```html
<form th:action="@{/signIn}" method="post">
  <!-- Thymeleaf automatically adds this -->
  <input
    type="hidden"
    th:name="${_csrf.parameterName}"
    th:value="${_csrf.token}"
  />
  <!-- Form fields -->
</form>
```

### CSRF Token Flow:

1. GET request to form page → Server generates CSRF token
2. Token embedded in form as hidden field
3. POST request includes token → Server validates token
4. Valid token → Process request
5. Invalid/missing token → 403 Forbidden

## 🔄 Remember-Me Functionality

### How Remember-Me Works:

1. **User Checks "Remember Me"**: Form includes `remember-me=on`

2. **Token Creation**: Spring Security creates encrypted token:

   ```
   Base64(username:expirationTime:hash)
   ```

3. **Cookie Storage**: Token stored in HttpOnly cookie:

   ```
   Set-Cookie: remember-me=token; Max-Age=86400; HttpOnly
   ```

4. **Automatic Re-authentication**: When session expires:
   - Remember-me filter checks for cookie
   - Validates token and expiration
   - Automatically logs user back in
   - Creates new session

### Security Features:

- **HttpOnly**: Prevents JavaScript access to cookie
- **Encrypted Token**: Contains hash to prevent tampering
- **Expiration**: Configurable timeout (24 hours in our case)
- **User Validation**: Re-validates user exists and is enabled

## 🧪 Testing & Validation

### Manual Testing Scenarios:

1. **Basic Login Test**:

   ```bash
   # Get CSRF token
   curl -c cookies.txt http://localhost:8080/signIn

   # Login with credentials
   curl -b cookies.txt -X POST http://localhost:8080/signIn \
     -d "username=admin&password=password123&_csrf=TOKEN"
   ```

2. **Remember-Me Test**:

   ```bash
   # Login with remember-me
   curl -c cookies.txt -X POST http://localhost:8080/signIn \
     -d "username=admin&password=password123&remember-me=on&_csrf=TOKEN"

   # Test access with only remember-me cookie
   curl -H "Cookie: remember-me=TOKEN" http://localhost:8080/profile
   ```

3. **Role-Based Access Test**:

   ```bash
   # Admin access (should work)
   curl -b admin_cookies.txt http://localhost:8080/admin/panel/halls

   # User access to admin page (should fail)
   curl -b user_cookies.txt http://localhost:8080/admin/panel/halls
   ```

### Expected Results:

- ✅ Valid credentials → Redirect to profile/admin panel
- ❌ Invalid credentials → Redirect to `/signIn?error=true`
- ✅ Remember-me cookie → Automatic re-authentication
- ❌ Wrong role → 403 Forbidden
- ❌ Missing CSRF token → 403 Forbidden

## 🔧 Troubleshooting Guide

### Common Issues & Solutions:

1. **Login Always Fails**:

   - Check password encoding (BCrypt vs plain text)
   - Verify user status is CONFIRMED
   - Check UserDetailsService implementation

2. **CSRF Token Errors**:

   - Ensure forms include CSRF token
   - Check CSRF is enabled in SecurityConfig
   - Verify Thymeleaf namespace in templates

3. **Remember-Me Not Working**:

   - Check checkbox name is "remember-me"
   - Verify UserDetailsService is configured
   - Check cookie expiration settings

4. **Access Denied Errors**:
   - Verify role names have "ROLE\_" prefix
   - Check URL patterns in SecurityConfig
   - Confirm user has correct role

### Debug Tips:

- Enable Spring Security debug logging
- Check browser developer tools for cookies
- Verify database user data
- Test with curl to isolate issues

## 📚 Key Learning Points

### Spring Security Concepts Mastered:

1. **Custom Authentication**: UserDetails and UserDetailsService
2. **Authorization**: Role-based access control
3. **Security Configuration**: Declarative security setup
4. **CSRF Protection**: Understanding and implementation
5. **Remember-Me**: Persistent authentication tokens
6. **Password Security**: BCrypt encryption

### Best Practices Applied:

- Separation of concerns (UserPrincipal wraps User)
- Secure password storage (BCrypt)
- HttpOnly cookies for security
- Proper error handling
- Clean security configuration

### Real-World Applications:

- User management systems
- Role-based dashboards
- Secure web applications
- Enterprise authentication systems

This implementation provides a solid foundation for understanding Spring Security and can be extended for more complex authentication scenarios.
