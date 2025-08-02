# 🎬 Cinema Spring Boot - Exercise 01: Internationalization & Form Validation

## 📋 Table of Contents

1. [Overview](#overview)
2. [Requirements Analysis](#requirements-analysis)
3. [Implementation Steps](#implementation-steps)
4. [Internationalization Setup](#internationalization-setup)
5. [Form Validation Implementation](#form-validation-implementation)
6. [Custom Password Validator](#custom-password-validator)
7. [Localization Configuration](#localization-configuration)
8. [Testing & Validation](#testing--validation)
9. [Troubleshooting Guide](#troubleshooting-guide)
10. [Complete Code Examples](#complete-code-examples)

## 🎯 Overview

This exercise builds upon Exercise 00's Spring Security foundation to implement **Internationalization (i18n)** and **comprehensive form validation**. We'll create a multi-language cinema application with localized error messages and robust form validation.

### Key Features to Implement:

- 🌍 **Multi-language Support**: At least 2 languages (English & French/Spanish/Arabic)
- 🍪 **Cookie-based Locale Storage**: Persistent language selection
- ✅ **Form Validation**: Registration form with comprehensive validation rules
- 🔒 **Custom Password Validator**: Using `@ValidPassword` annotation
- 📝 **Localized Error Messages**: Validation errors in selected language
- 🎨 **Localized UI**: At least 3 pages with full localization support

## 📝 Requirements Analysis

### Core Requirements for Exercise 01:

1. **Multiple Language Support**: Implement at least 2 languages of your choice
2. **Locale Change via URL Parameter**: `?lang=en` or `?lang=fr` parameter support
3. **Cookie-based Persistence**: Store locale selection in browser cookies
4. **Localized Validation Messages**: Form errors displayed in selected language
5. **Registration Form Validation**:
   - First and last name: non-empty
   - Email: valid email format
   - Phone: `+(code)digits` pattern (e.g., `+7(777)777777`)
   - Password: uppercase, lowercase, digit, min 8 characters
6. **Custom Password Validator**: `@ValidPassword` annotation with `ConstraintValidator`

### Technical Requirements:

- ✅ Properties files for localization and error messages
- ✅ LocaleResolver, LocaleChangeInterceptor configuration
- ✅ LocalValidatorFactoryBean, MessageSource, MessageCodesResolver beans
- ✅ `javax.validation.constraints.*` annotations
- ✅ Custom `@ValidPassword` annotation with validator implementation

## 🚀 Implementation Steps

### Step 1: Create Localization Configuration

Create the main configuration class for internationalization:

**Create: `src/main/java/com/cinema/config/LocalizationConfig.java`**

```java
@Configuration
@EnableWebMvc
public class LocalizationConfig implements WebMvcConfigurer {

    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setDefaultLocale(Locale.ENGLISH);
        resolver.setCookieName("cinema-locale");
        resolver.setCookieMaxAge(3600 * 24 * 30); // 30 days
        return resolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource =
            new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(3600);
        return messageSource;
    }

    @Bean
    public LocalValidatorFactoryBean getValidator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource());
        return bean;
    }

    @Bean
    public MessageCodesResolver messageCodesResolver() {
        DefaultMessageCodesResolver resolver = new DefaultMessageCodesResolver();
        resolver.setMessageCodeFormatter(DefaultMessageCodesResolver.Format.POSTFIX_ERROR_CODE);
        return resolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}
```

### Step 2: Create Properties Files for Localization

Create message properties files for different languages:

**Create: `src/main/resources/messages.properties` (Default - English)**

```properties
# Navigation
nav.home=Home
nav.profile=Profile
nav.admin=Admin Panel
nav.signin=Sign In
nav.signup=Sign Up
nav.logout=Logout
nav.language=Language

# Common
common.welcome=Welcome
common.submit=Submit
common.cancel=Cancel
common.save=Save
common.edit=Edit
common.delete=Delete
common.back=Back

# Authentication
auth.signin.title=Sign In
auth.signin.username=Username
auth.signin.password=Password
auth.signin.remember=Remember me
auth.signin.submit=Sign In
auth.signup.title=Sign Up
auth.signup.firstname=First Name
auth.signup.lastname=Last Name
auth.signup.username=Username
auth.signup.email=Email
auth.signup.phone=Phone Number
auth.signup.password=Password
auth.signup.confirmpassword=Confirm Password
auth.signup.submit=Create Account

# Validation Error Messages
errors.required=This field is required
errors.email.invalid=Please provide a valid email address
errors.phone.invalid=Phone number must match +(code)digits pattern, e.g., +7(777)777777
errors.password.mismatch=Passwords do not match
errors.username.exists=Username already exists
errors.email.exists=Email already exists
errors.incorrect.password=Password must contain uppercase, lowercase letters, and at least one digit; minimum 8 characters
```

### Step 3: Create French Localization

**Create: `src/main/resources/messages_fr.properties` (French)**

```properties
# Navigation
nav.home=Accueil
nav.profile=Profil
nav.admin=Panneau d'administration
nav.signin=Se connecter
nav.signup=S'inscrire
nav.logout=Déconnexion
nav.language=Langue

# Common
common.welcome=Bienvenue
common.submit=Soumettre
common.cancel=Annuler
common.save=Enregistrer
common.edit=Modifier
common.delete=Supprimer
common.back=Retour

# Authentication
auth.signin.title=Se connecter
auth.signin.username=Nom d'utilisateur
auth.signin.password=Mot de passe
auth.signin.remember=Se souvenir de moi
auth.signin.submit=Se connecter
auth.signup.title=S'inscrire
auth.signup.firstname=Prénom
auth.signup.lastname=Nom de famille
auth.signup.username=Nom d'utilisateur
auth.signup.email=Email
auth.signup.phone=Numéro de téléphone
auth.signup.password=Mot de passe
auth.signup.confirmpassword=Confirmer le mot de passe
auth.signup.submit=Créer un compte

# Validation Error Messages
errors.required=Ce champ est obligatoire
errors.email.invalid=Veuillez fournir une adresse email valide
errors.phone.invalid=Le numéro de téléphone doit correspondre au format +(code)chiffres, ex: +7(777)777777
errors.password.mismatch=Les mots de passe ne correspondent pas
errors.username.exists=Ce nom d'utilisateur existe déjà
errors.email.exists=Cette adresse email existe déjà
errors.incorrect.password=Le mot de passe doit contenir des lettres majuscules, minuscules et au moins un chiffre; minimum 8 caractères
```

## 🔒 Custom Password Validator

### Step 4: Create Custom Password Validation Annotation

**Create: `src/main/java/com/cinema/validation/ValidPassword.java`**

```java
@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "{errors.incorrect.password}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

### Step 5: Implement Password Validator

**Create: `src/main/java/com/cinema/validation/PasswordValidator.java`**

```java
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        // Initialization logic if needed
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }

        // Check minimum length
        if (password.length() < 8) {
            return false;
        }

        // Check for uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            return false;
        }

        // Check for lowercase letter
        if (!password.matches(".*[a-z].*")) {
            return false;
        }

        // Check for digit
        if (!password.matches(".*\\d.*")) {
            return false;
        }

        return true;
    }
}
```

## 📋 Form Validation Implementation

### Step 6: Create Registration DTO with Validation

**Create: `src/main/java/com/cinema/dto/UserRegistrationDto.java`**

```java
public class UserRegistrationDto {

    @NotBlank(message = "{errors.required}")
    @Size(min = 2, max = 50, message = "{errors.name.size}")
    private String firstName;

    @NotBlank(message = "{errors.required}")
    @Size(min = 2, max = 50, message = "{errors.name.size}")
    private String lastName;

    @NotBlank(message = "{errors.required}")
    @Size(min = 3, max = 50, message = "{errors.username.size}")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "{errors.username.pattern}")
    private String username;

    @NotBlank(message = "{errors.required}")
    @Email(message = "{errors.email.invalid}")
    private String email;

    @Pattern(regexp = "^\\+\\d{1,3}\\(\\d{3}\\)\\d{7}$", message = "{errors.phone.invalid}")
    private String phone;

    @ValidPassword(message = "{errors.incorrect.password}")
    private String password;

    @NotBlank(message = "{errors.required}")
    private String confirmPassword;

    // Constructors, getters, and setters
}
```

## 🎮 Controller Implementation

### Step 7: Update AuthController for Registration

**Update: `src/main/java/com/cinema/controller/AuthController.java`**

```java
@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messageSource;

    @GetMapping("/signUp")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userRegistrationDto", new UserRegistrationDto());
        return "auth/signUp";
    }

    @PostMapping("/signUp")
    public String processRegistration(@Valid @ModelAttribute("userRegistrationDto") UserRegistrationDto registrationDto,
                                    BindingResult bindingResult,
                                    Model model,
                                    HttpServletRequest request,
                                    RedirectAttributes redirectAttributes) {

        // Check if passwords match
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "errors.password.mismatch");
        }

        // Check if username already exists
        if (userService.existsByUsername(registrationDto.getUsername())) {
            bindingResult.rejectValue("username", "errors.username.exists");
        }

        // Check if email already exists
        if (userService.existsByEmail(registrationDto.getEmail())) {
            bindingResult.rejectValue("email", "errors.email.exists");
        }

        if (bindingResult.hasErrors()) {
            return "auth/signUp";
        }

        try {
            userService.registerUser(registrationDto);

            Locale locale = LocaleContextHolder.getLocale();
            String successMessage = messageSource.getMessage("auth.signup.success", null, locale);
            redirectAttributes.addFlashAttribute("successMessage", successMessage);

            return "redirect:/signIn";
        } catch (Exception e) {
            Locale locale = LocaleContextHolder.getLocale();
            String errorMessage = messageSource.getMessage("auth.signup.error", null, locale);
            model.addAttribute("errorMessage", errorMessage);
            return "auth/signUp";
        }
    }
}
```

### Step 8: Create UserService for Registration Logic

**Create: `src/main/java/com/cinema/service/UserService.java`**

```java
@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(UserRegistrationDto registrationDto) {
        User user = new User();
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPhone(registrationDto.getPhone());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setRole(User.Role.USER);
        user.setStatus(User.Status.CONFIRMED); // For simplicity, auto-confirm
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
```

## 🎨 Template Updates for Localization

### Step 9: Update Base Template with Language Switcher

**Update: `src/main/resources/templates/layout/base.html`**

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title th:text="#{nav.home} + ' - Cinema'">Cinema</title>
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css"
      rel="stylesheet"
    />
  </head>
  <body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
      <div class="container">
        <a class="navbar-brand" href="/" th:text="#{nav.home}">Home</a>

        <div class="navbar-nav ms-auto">
          <!-- Language Switcher -->
          <div class="nav-item dropdown">
            <a
              class="nav-link dropdown-toggle"
              href="#"
              role="button"
              data-bs-toggle="dropdown"
            >
              <span th:text="#{nav.language}">Language</span>
            </a>
            <ul class="dropdown-menu">
              <li>
                <a class="dropdown-item" th:href="@{''(lang=en)}">English</a>
              </li>
              <li>
                <a class="dropdown-item" th:href="@{''(lang=fr)}">Français</a>
              </li>
            </ul>
          </div>

          <!-- Authentication Links -->
          <div sec:authorize="!isAuthenticated()">
            <a class="nav-link" th:href="@{/signIn}" th:text="#{nav.signin}"
              >Sign In</a
            >
            <a class="nav-link" th:href="@{/signUp}" th:text="#{nav.signup}"
              >Sign Up</a
            >
          </div>

          <div sec:authorize="isAuthenticated()">
            <a class="nav-link" th:href="@{/profile}" th:text="#{nav.profile}"
              >Profile</a
            >
            <form th:action="@{/logout}" method="post" class="d-inline">
              <button
                type="submit"
                class="btn btn-link nav-link"
                th:text="#{nav.logout}"
              >
                Logout
              </button>
            </form>
          </div>
        </div>
      </div>
    </nav>

    <main class="container mt-4">
      <div th:fragment="content">
        <!-- Page content will be inserted here -->
      </div>
    </main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
  </body>
</html>
```

### Step 10: Update Registration Form Template

**Update: `src/main/resources/templates/auth/signUp.html`**

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title th:text="#{auth.signup.title} + ' - Cinema'">Sign Up - Cinema</title>
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css"
      rel="stylesheet"
    />
  </head>
  <body>
    <div class="container mt-5">
      <div class="row justify-content-center">
        <div class="col-md-6">
          <div class="card">
            <div class="card-header">
              <h3 class="text-center" th:text="#{auth.signup.title}">
                Sign Up
              </h3>
            </div>
            <div class="card-body">
              <!-- Language Switcher -->
              <div class="mb-3 text-center">
                <a
                  th:href="@{/signUp(lang=en)}"
                  class="btn btn-sm btn-outline-primary me-2"
                  >English</a
                >
                <a
                  th:href="@{/signUp(lang=fr)}"
                  class="btn btn-sm btn-outline-primary"
                  >Français</a
                >
              </div>

              <!-- Success/Error Messages -->
              <div
                th:if="${successMessage}"
                class="alert alert-success"
                th:text="${successMessage}"
              ></div>
              <div
                th:if="${errorMessage}"
                class="alert alert-danger"
                th:text="${errorMessage}"
              ></div>

              <form
                th:action="@{/signUp}"
                th:object="${userRegistrationDto}"
                method="post"
              >
                <!-- First Name -->
                <div class="mb-3">
                  <label
                    th:for="firstName"
                    class="form-label"
                    th:text="#{auth.signup.firstname}"
                    >First Name</label
                  >
                  <input
                    type="text"
                    class="form-control"
                    th:field="*{firstName}"
                    th:classappend="${#fields.hasErrors('firstName')} ? 'is-invalid' : ''"
                  />
                  <div
                    class="invalid-feedback"
                    th:if="${#fields.hasErrors('firstName')}"
                    th:errors="*{firstName}"
                  ></div>
                </div>

                <!-- Last Name -->
                <div class="mb-3">
                  <label
                    th:for="lastName"
                    class="form-label"
                    th:text="#{auth.signup.lastname}"
                    >Last Name</label
                  >
                  <input
                    type="text"
                    class="form-control"
                    th:field="*{lastName}"
                    th:classappend="${#fields.hasErrors('lastName')} ? 'is-invalid' : ''"
                  />
                  <div
                    class="invalid-feedback"
                    th:if="${#fields.hasErrors('lastName')}"
                    th:errors="*{lastName}"
                  ></div>
                </div>

                <!-- Username -->
                <div class="mb-3">
                  <label
                    th:for="username"
                    class="form-label"
                    th:text="#{auth.signup.username}"
                    >Username</label
                  >
                  <input
                    type="text"
                    class="form-control"
                    th:field="*{username}"
                    th:classappend="${#fields.hasErrors('username')} ? 'is-invalid' : ''"
                  />
                  <div
                    class="invalid-feedback"
                    th:if="${#fields.hasErrors('username')}"
                    th:errors="*{username}"
                  ></div>
                </div>

                <!-- Email -->
                <div class="mb-3">
                  <label
                    th:for="email"
                    class="form-label"
                    th:text="#{auth.signup.email}"
                    >Email</label
                  >
                  <input
                    type="email"
                    class="form-control"
                    th:field="*{email}"
                    th:classappend="${#fields.hasErrors('email')} ? 'is-invalid' : ''"
                  />
                  <div
                    class="invalid-feedback"
                    th:if="${#fields.hasErrors('email')}"
                    th:errors="*{email}"
                  ></div>
                </div>

                <!-- Phone -->
                <div class="mb-3">
                  <label
                    th:for="phone"
                    class="form-label"
                    th:text="#{auth.signup.phone}"
                    >Phone Number</label
                  >
                  <input
                    type="tel"
                    class="form-control"
                    th:field="*{phone}"
                    placeholder="+7(777)777777"
                    th:classappend="${#fields.hasErrors('phone')} ? 'is-invalid' : ''"
                  />
                  <div
                    class="invalid-feedback"
                    th:if="${#fields.hasErrors('phone')}"
                    th:errors="*{phone}"
                  ></div>
                </div>

                <!-- Password -->
                <div class="mb-3">
                  <label
                    th:for="password"
                    class="form-label"
                    th:text="#{auth.signup.password}"
                    >Password</label
                  >
                  <input
                    type="password"
                    class="form-control"
                    th:field="*{password}"
                    th:classappend="${#fields.hasErrors('password')} ? 'is-invalid' : ''"
                  />
                  <div
                    class="invalid-feedback"
                    th:if="${#fields.hasErrors('password')}"
                    th:errors="*{password}"
                  ></div>
                </div>

                <!-- Confirm Password -->
                <div class="mb-3">
                  <label
                    th:for="confirmPassword"
                    class="form-label"
                    th:text="#{auth.signup.confirmpassword}"
                    >Confirm Password</label
                  >
                  <input
                    type="password"
                    class="form-control"
                    th:field="*{confirmPassword}"
                    th:classappend="${#fields.hasErrors('confirmPassword')} ? 'is-invalid' : ''"
                  />
                  <div
                    class="invalid-feedback"
                    th:if="${#fields.hasErrors('confirmPassword')}"
                    th:errors="*{confirmPassword}"
                  ></div>
                </div>

                <div class="d-grid">
                  <button
                    type="submit"
                    class="btn btn-primary"
                    th:text="#{auth.signup.submit}"
                  >
                    Create Account
                  </button>
                </div>
              </form>

              <div class="text-center mt-3">
                <a th:href="@{/signIn}" th:text="#{auth.signin.title}"
                  >Already have an account? Sign In</a
                >
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
  </body>
</html>
```

## 🧪 Testing & Validation

### Step 11: Testing the Implementation

1. **Start the Application**:

   ```bash
   cd ex01
   mvn spring-boot:run
   ```

2. **Test Language Switching**:

   - Visit `http://localhost:8080/signUp`
   - Click on language switcher (English/Français)
   - Verify that all text changes to the selected language
   - Check that the language preference is stored in cookies

3. **Test Form Validation**:

   - Try submitting empty form - should show localized error messages
   - Test invalid email format
   - Test invalid phone number format (should match `+X(XXX)XXXXXXX`)
   - Test weak password (missing uppercase, lowercase, or digit)
   - Test password mismatch
   - Test duplicate username/email

4. **Test Successful Registration**:
   - Fill form with valid data:
     - First Name: `John`
     - Last Name: `Doe`
     - Username: `johndoe`
     - Email: `john@example.com`
     - Phone: `+1(555)1234567`
     - Password: `Password123`
   - Should redirect to sign-in page with success message

### Step 12: Add Missing Properties

Add these additional properties to your message files:

**Add to `messages.properties`:**

```properties
# Additional validation messages
errors.name.size=Name must be between 2 and 50 characters
errors.username.size=Username must be between 3 and 50 characters
errors.username.pattern=Username can only contain letters, numbers, and underscores

# Success/Error messages
auth.signup.success=Account created successfully! Please sign in.
auth.signup.error=An error occurred during registration. Please try again.
```

**Add to `messages_fr.properties`:**

```properties
# Additional validation messages
errors.name.size=Le nom doit contenir entre 2 et 50 caractères
errors.username.size=Le nom d'utilisateur doit contenir entre 3 et 50 caractères
errors.username.pattern=Le nom d'utilisateur ne peut contenir que des lettres, chiffres et underscores

# Success/Error messages
auth.signup.success=Compte créé avec succès! Veuillez vous connecter.
auth.signup.error=Une erreur s'est produite lors de l'inscription. Veuillez réessayer.
```

## 🔧 Troubleshooting Guide

### Common Issues and Solutions:

1. **Validation Messages Not Localized**:

   - Ensure `LocalValidatorFactoryBean` is configured with `MessageSource`
   - Check that message keys in validation annotations match properties files
   - Verify properties files are in `src/main/resources/`

2. **Language Not Persisting**:

   - Check `CookieLocaleResolver` configuration
   - Verify cookie name and max age settings
   - Ensure `LocaleChangeInterceptor` is registered

3. **Custom Validator Not Working**:

   - Verify `@ValidPassword` annotation is properly defined
   - Check `PasswordValidator` implements `ConstraintValidator<ValidPassword, String>`
   - Ensure validator is in component scan path

4. **Form Validation Errors**:

   - Check `@Valid` annotation on controller method parameter
   - Verify `BindingResult` parameter follows the validated object
   - Ensure error handling in templates uses `th:errors`

5. **Database Constraint Violations**:
   - Add proper unique constraint checks in service layer
   - Handle `DataIntegrityViolationException` appropriately
   - Provide user-friendly error messages

## 📚 Key Learning Points

### Internationalization (i18n) Concepts:

1. **Locale Resolution**: How Spring determines user's preferred language
2. **Message Interpolation**: Using `MessageSource` to resolve localized messages
3. **Cookie-based Persistence**: Storing user preferences across sessions
4. **Template Localization**: Using Thymeleaf's `#{}` syntax for message resolution

### Validation Framework:

1. **Bean Validation (JSR-303)**: Standard Java validation annotations
2. **Custom Validators**: Creating domain-specific validation logic
3. **Error Message Localization**: Connecting validation errors to i18n system
4. **Form Binding**: Spring MVC's data binding and validation integration

### Best Practices Implemented:

- ✅ Separation of concerns (DTO vs Entity)
- ✅ Proper error handling and user feedback
- ✅ Consistent naming conventions for message keys
- ✅ Responsive form design with Bootstrap
- ✅ Security considerations (password encoding, CSRF protection)

## 🎯 Exercise Completion Checklist

- [ ] **LocalizationConfig** created with all required beans
- [ ] **Properties files** created for at least 2 languages
- [ ] **Custom @ValidPassword** annotation and validator implemented
- [ ] **UserRegistrationDto** with proper validation annotations
- [ ] **AuthController** updated with registration logic
- [ ] **UserService** created for user management
- [ ] **Templates** updated with localization support
- [ ] **Language switcher** implemented and working
- [ ] **Form validation** working with localized error messages
- [ ] **Cookie-based locale** persistence working
- [ ] **At least 3 pages** fully localized
- [ ] **Phone number validation** matches required pattern
- [ ] **Password validation** includes all requirements
- [ ] **Registration flow** complete and functional

## 🚀 Next Steps

After completing Exercise 01, you should have:

1. A fully functional multi-language cinema application
2. Comprehensive form validation with custom validators
3. Localized error messages and user interface
4. Cookie-based language preference persistence
5. A solid foundation for Exercise 02 and beyond

**Ready for Exercise 02?** The next exercise will likely build upon this internationalization and validation foundation to add more advanced features like email confirmation, advanced user management, or cinema-specific functionality.

---

**🎉 Congratulations!** You've successfully implemented internationalization and form validation in your Spring Boot cinema application. The application now supports multiple languages with persistent user preferences and robust form validation with localized error messages.
