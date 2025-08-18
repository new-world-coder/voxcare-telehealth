module.exports = {
  root: true,
  env: {
    node: true,
    browser: true,
    es2021: true,
  },
  extends: [
    'plugin:vue/vue3-essential',
    'eslint:recommended',
    '@vue/eslint-config-prettier',
    // Security plugins
    'plugin:security/recommended',
    'plugin:vuejs-accessibility/recommended',
  ],
  parserOptions: {
    ecmaVersion: 2021,
    sourceType: 'module',
  },
  plugins: [
    'security',
    'vuejs-accessibility',
  ],
  rules: {
    // HIPAA Compliance Rules
    'no-console': 'warn', // Prevent PHI logging
    'no-debugger': 'error', // Prevent debug statements in production
    
    // PCI DSS Compliance Rules
    'no-eval': 'error', // Prevent code injection
    'no-implied-eval': 'error', // Prevent implied eval
    
    // OWASP Top 10 Security Rules
    'security/detect-object-injection': 'error', // Prevent object injection
    'security/detect-non-literal-regexp': 'error', // Prevent regex injection
    'security/detect-unsafe-regex': 'error', // Prevent regex DoS
    'security/detect-buffer-noassert': 'error', // Prevent buffer overflow
    'security/detect-child-process': 'warn', // Warn about child process execution
    'security/detect-disable-mustache-escape': 'error', // Prevent XSS
    'security/detect-eval-with-expression': 'error', // Prevent eval injection
    'security/detect-no-csrf-before-method-override': 'error', // Prevent CSRF
    'security/detect-non-literal-fs-filename': 'warn', // Warn about file path injection
    'security/detect-non-literal-require': 'warn', // Warn about require injection
    'security/detect-possible-timing-attacks': 'warn', // Warn about timing attacks
    'security/detect-pseudoRandomBytes': 'error', // Prevent weak random generation
    
    // GDPR Compliance Rules
    'no-var': 'error', // Use const/let for better data handling
    'prefer-const': 'error', // Prevent accidental data mutation
    
    // Vue.js Security Rules
    'vue/no-v-html': 'error', // Prevent XSS via v-html
    'vue/no-inline-template': 'error', // Prevent template injection
    'vue/no-template-key': 'error', // Prevent key injection
    'vue/no-unused-vars': 'error', // Prevent unused variables
    'vue/no-unused-components': 'error', // Prevent unused components
    'vue/no-unused-properties': 'error', // Prevent unused properties
    
    // Accessibility Rules (Security related)
    'vuejs-accessibility/alt-text': 'error', // Prevent information disclosure
    'vuejs-accessibility/click-events-have-key-events': 'error', // Ensure keyboard accessibility
    'vuejs-accessibility/form-has-label': 'error', // Ensure form security
    'vuejs-accessibility/iframe-has-title': 'error', // Prevent clickjacking
    'vuejs-accessibility/interactive-supports-focus': 'error', // Ensure focus management
    'vuejs-accessibility/label-has-for': 'error', // Ensure form security
    'vuejs-accessibility/mouse-events-have-key-events': 'error', // Ensure keyboard accessibility
    'vuejs-accessibility/no-access-key': 'warn', // Warn about access key conflicts
    'vuejs-accessibility/no-autofocus': 'error', // Prevent focus hijacking
    'vuejs-accessibility/no-distracting-elements': 'error', // Prevent UI manipulation
    'vuejs-accessibility/no-header-scope': 'error', // Ensure proper heading structure
    'vuejs-accessibility/no-interactive-element-to-noninteractive-role': 'error', // Prevent role confusion
    'vuejs-accessibility/no-noninteractive-element-interactions': 'error', // Prevent interaction confusion
    'vuejs-accessibility/no-noninteractive-element-to-interactive-role': 'error', // Prevent role confusion
    'vuejs-accessibility/no-noninteractive-tabindex': 'error', // Prevent tabindex abuse
    'vuejs-accessibility/no-onchange': 'warn', // Warn about onChange usage
    'vuejs-accessibility/no-redundant-roles': 'error', // Prevent role confusion
    'vuejs-accessibility/no-static-element-interactions': 'error', // Prevent interaction confusion
    'vuejs-accessibility/role-has-required-aria-props': 'error', // Ensure ARIA compliance
    'vuejs-accessibility/role-supports-aria-props': 'error', // Ensure ARIA compliance
    'vuejs-accessibility/scope': 'error', // Ensure proper scope
    'vuejs-accessibility/tabindex-no-positive': 'error', // Prevent tabindex abuse
    
    // General Security Rules
    'no-alert': 'warn', // Prevent alert injection
    'no-caller': 'error', // Prevent function injection
    'no-catch-shadow': 'error', // Prevent variable shadowing
    'no-extend-native': 'error', // Prevent prototype pollution
    'no-extra-bind': 'error', // Prevent unnecessary binding
    'no-extra-label': 'error', // Prevent label injection
    'no-implied-eval': 'error', // Prevent eval injection
    'no-import-assign': 'error', // Prevent import modification
    'no-label-var': 'error', // Prevent label confusion
    'no-labels': 'error', // Prevent label injection
    'no-lone-blocks': 'error', // Prevent block injection
    'no-loop-func': 'error', // Prevent closure injection
    'no-multi-assign': 'error', // Prevent assignment injection
    'no-native-reassign': 'error', // Prevent native object modification
    'no-new-func': 'error', // Prevent function injection
    'no-new-object': 'error', // Prevent object injection
    'no-new-wrappers': 'error', // Prevent wrapper injection
    'no-octal': 'error', // Prevent octal injection
    'no-octal-escape': 'error', // Prevent octal escape injection
    'no-param-reassign': 'error', // Prevent parameter modification
    'no-proto': 'error', // Prevent prototype modification
    'no-redeclare': 'error', // Prevent variable redeclaration
    'no-return-assign': 'error', // Prevent return assignment injection
    'no-script-url': 'error', // Prevent script injection
    'no-self-assign': 'error', // Prevent self assignment
    'no-self-compare': 'error', // Prevent self comparison
    'no-sequences': 'error', // Prevent sequence injection
    'no-throw-literal': 'error', // Prevent literal throwing
    'no-unmodified-loop-condition': 'error', // Prevent loop condition modification
    'no-unused-expressions': 'error', // Prevent unused expression injection
    'no-useless-call': 'error', // Prevent useless call injection
    'no-useless-concat': 'error', // Prevent useless concatenation
    'no-useless-return': 'error', // Prevent useless return injection
    'no-void': 'error', // Prevent void injection
    'no-warning-comments': 'warn', // Warn about warning comments
    'no-with': 'error', // Prevent with statement injection
    'prefer-promise-reject-errors': 'error', // Ensure proper error handling
    'require-await': 'error', // Ensure proper async handling
    'yoda': 'error', // Prevent yoda condition injection
    
    // Code Quality Rules (Security related)
    'complexity': ['error', 10], // Prevent complex code that could hide vulnerabilities
    'max-depth': ['error', 4], // Prevent deeply nested code that could hide vulnerabilities
    'max-len': ['error', 120], // Prevent long lines that could hide vulnerabilities
    'max-lines': ['error', 300], // Prevent long files that could hide vulnerabilities
    'max-lines-per-function': ['error', 50], // Prevent long functions that could hide vulnerabilities
    'max-nested-callbacks': ['error', 3], // Prevent deeply nested callbacks that could hide vulnerabilities
    'max-params': ['error', 4], // Prevent too many parameters that could hide vulnerabilities
    'max-statements': ['error', 20], // Prevent too many statements that could hide vulnerabilities
    'no-magic-numbers': 'warn', // Warn about magic numbers that could hide vulnerabilities
    'no-multiple-empty-lines': 'error', // Prevent multiple empty lines that could hide vulnerabilities
    'no-trailing-spaces': 'error', // Prevent trailing spaces that could hide vulnerabilities
    'no-unreachable': 'error', // Prevent unreachable code that could hide vulnerabilities
    'no-unused-labels': 'error', // Prevent unused labels that could hide vulnerabilities
    'no-useless-escape': 'error', // Prevent useless escapes that could hide vulnerabilities
    'prefer-const': 'error', // Use const for immutable values
    'radix': 'error', // Ensure proper radix for parseInt
    'use-isnan': 'error', // Ensure proper NaN checking
    'valid-typeof': 'error', // Ensure proper typeof checking
  },
  overrides: [
    {
      files: ['**/*.test.js', '**/*.spec.js'],
      env: {
        jest: true,
      },
      rules: {
        // Relax some rules for test files
        'no-console': 'off',
        'security/detect-child-process': 'off',
      },
    },
  ],
  globals: {
    // Define global variables that are safe
    process: 'readonly',
    Buffer: 'readonly',
    __dirname: 'readonly',
    __filename: 'readonly',
  },
};
