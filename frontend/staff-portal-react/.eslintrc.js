module.exports = {
  root: true,
  env: {
    browser: true,
    es2021: true,
    node: true,
  },
  extends: [
    'eslint:recommended',
    'plugin:react/recommended',
    'plugin:react-hooks/recommended',
    'plugin:@typescript-eslint/recommended',
    'plugin:jsx-a11y/recommended',
    // Security plugins
    'plugin:security/recommended',
    'plugin:react-security/recommended',
  ],
  parser: '@typescript-eslint/parser',
  parserOptions: {
    ecmaFeatures: {
      jsx: true,
    },
    ecmaVersion: 2021,
    sourceType: 'module',
  },
  plugins: [
    'react',
    'react-hooks',
    '@typescript-eslint',
    'jsx-a11y',
    'security',
    'react-security',
  ],
  settings: {
    react: {
      version: 'detect',
    },
  },
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
    
    // React Security Rules
    'react-security/no-dangerously-set-innerhtml': 'error', // Prevent XSS via dangerouslySetInnerHTML
    'react-security/no-unsafe-iframe-src': 'error', // Prevent clickjacking
    'react-security/no-unsafe-object-assign': 'error', // Prevent object injection
    'react-security/no-unsafe-regex': 'error', // Prevent regex DoS
    'react-security/no-unsafe-url': 'error', // Prevent URL injection
    'react-security/no-unsafe-window-open': 'error', // Prevent window.open injection
    'react-security/no-unsafe-xss': 'error', // Prevent XSS
    'react-security/no-unsafe-callback-ref': 'error', // Prevent callback injection
    'react-security/no-unsafe-component-will-mount': 'error', // Prevent lifecycle injection
    'react-security/no-unsafe-component-will-receive-props': 'error', // Prevent props injection
    'react-security/no-unsafe-component-will-update': 'error', // Prevent update injection
    'react-security/no-unsafe-constructor': 'error', // Prevent constructor injection
    'react-security/no-unsafe-default-props': 'error', // Prevent default props injection
    'react-security/no-unsafe-jsx-arrow-function': 'error', // Prevent arrow function injection
    'react-security/no-unsafe-jsx-bind': 'error', // Prevent bind injection
    'react-security/no-unsafe-jsx-closing-bracket-location': 'error', // Prevent bracket injection
    'react-security/no-unsafe-jsx-indent': 'error', // Prevent indent injection
    'react-security/no-unsafe-jsx-indent-props': 'error', // Prevent props indent injection
    'react-security/no-unsafe-jsx-max-props-per-line': 'error', // Prevent props per line injection
    'react-security/no-unsafe-jsx-no-bind': 'error', // Prevent bind injection
    'react-security/no-unsafe-jsx-no-comment-textnodes': 'error', // Prevent comment injection
    'react-security/no-unsafe-jsx-no-duplicate-props': 'error', // Prevent duplicate props injection
    'react-security/no-unsafe-jsx-no-literals': 'error', // Prevent literal injection
    'react-security/no-unsafe-jsx-no-target-blank': 'error', // Prevent target blank injection
    'react-security/no-unsafe-jsx-pascal-case': 'error', // Prevent pascal case injection
    'react-security/no-unsafe-jsx-quotes': 'error', // Prevent quotes injection
    'react-security/no-unsafe-jsx-space-before-closing': 'error', // Prevent space injection
    'react-security/no-unsafe-jsx-wrap-multilines': 'error', // Prevent multiline injection
    
    // React Hooks Security Rules
    'react-hooks/rules-of-hooks': 'error', // Ensure proper hooks usage
    'react-hooks/exhaustive-deps': 'error', // Ensure proper dependencies
    
    // TypeScript Security Rules
    '@typescript-eslint/no-explicit-any': 'error', // Prevent any type usage
    '@typescript-eslint/no-unsafe-assignment': 'error', // Prevent unsafe assignment
    '@typescript-eslint/no-unsafe-call': 'error', // Prevent unsafe call
    '@typescript-eslint/no-unsafe-member-access': 'error', // Prevent unsafe member access
    '@typescript-eslint/no-unsafe-return': 'error', // Prevent unsafe return
    '@typescript-eslint/no-unsafe-argument': 'error', // Prevent unsafe argument
    '@typescript-eslint/no-unsafe-enum-comparison': 'error', // Prevent unsafe enum comparison
    '@typescript-eslint/no-unsafe-unary-negation': 'error', // Prevent unsafe unary negation
    '@typescript-eslint/no-unsafe-regexp': 'error', // Prevent unsafe regexp
    '@typescript-eslint/no-unsafe-optional-chaining': 'error', // Prevent unsafe optional chaining
    '@typescript-eslint/no-unsafe-non-null-assertion': 'error', // Prevent unsafe non-null assertion
    '@typescript-eslint/no-unsafe-type-assertion': 'error', // Prevent unsafe type assertion
    '@typescript-eslint/no-unsafe-type-predicate': 'error', // Prevent unsafe type predicate
    '@typescript-eslint/no-unsafe-typeof': 'error', // Prevent unsafe typeof
    '@typescript-eslint/no-unsafe-array-method': 'error', // Prevent unsafe array method
    '@typescript-eslint/no-unsafe-array-slice': 'error', // Prevent unsafe array slice
    '@typescript-eslint/no-unsafe-array-splice': 'error', // Prevent unsafe array splice
    '@typescript-eslint/no-unsafe-array-join': 'error', // Prevent unsafe array join
    '@typescript-eslint/no-unsafe-array-reverse': 'error', // Prevent unsafe array reverse
    '@typescript-eslint/no-unsafe-array-sort': 'error', // Prevent unsafe array sort
    '@typescript-eslint/no-unsafe-array-fill': 'error', // Prevent unsafe array fill
    '@typescript-eslint/no-unsafe-array-copywithin': 'error', // Prevent unsafe array copyWithin
    '@typescript-eslint/no-unsafe-array-pop': 'error', // Prevent unsafe array pop
    '@typescript-eslint/no-unsafe-array-push': 'error', // Prevent unsafe array push
    '@typescript-eslint/no-unsafe-array-shift': 'error', // Prevent unsafe array shift
    '@typescript-eslint/no-unsafe-array-unshift': 'error', // Prevent unsafe array unshift
    
    // Accessibility Rules (Security related)
    'jsx-a11y/alt-text': 'error', // Prevent information disclosure
    'jsx-a11y/click-events-have-key-events': 'error', // Ensure keyboard accessibility
    'jsx-a11y/form-has-label': 'error', // Ensure form security
    'jsx-a11y/iframe-has-title': 'error', // Prevent clickjacking
    'jsx-a11y/interactive-supports-focus': 'error', // Ensure focus management
    'jsx-a11y/label-has-for': 'error', // Ensure form security
    'jsx-a11y/mouse-events-have-key-events': 'error', // Ensure keyboard accessibility
    'jsx-a11y/no-access-key': 'warn', // Warn about access key conflicts
    'jsx-a11y/no-autofocus': 'error', // Prevent focus hijacking
    'jsx-a11y/no-distracting-elements': 'error', // Prevent UI manipulation
    'jsx-a11y/no-header-scope': 'error', // Ensure proper heading structure
    'jsx-a11y/no-interactive-element-to-noninteractive-role': 'error', // Prevent role confusion
    'jsx-a11y/no-noninteractive-element-interactions': 'error', // Prevent interaction confusion
    'jsx-a11y/no-noninteractive-element-to-interactive-role': 'error', // Prevent role confusion
    'jsx-a11y/no-noninteractive-tabindex': 'error', // Prevent tabindex abuse
    'jsx-a11y/no-onchange': 'warn', // Warn about onChange usage
    'jsx-a11y/no-redundant-roles': 'error', // Prevent role confusion
    'jsx-a11y/no-static-element-interactions': 'error', // Prevent interaction confusion
    'jsx-a11y/role-has-required-aria-props': 'error', // Ensure ARIA compliance
    'jsx-a11y/role-supports-aria-props': 'error', // Ensure ARIA compliance
    'jsx-a11y/scope': 'error', // Ensure proper scope
    'jsx-a11y/tabindex-no-positive': 'error', // Prevent tabindex abuse
    
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
    
    // React Specific Rules
    'react/prop-types': 'off', // Disable prop-types as we use TypeScript
    'react/react-in-jsx-scope': 'off', // Not needed in React 17+
    'react/display-name': 'off', // Disable display-name requirement
    'react/no-unescaped-entities': 'error', // Prevent HTML entity injection
    'react/no-unknown-property': 'error', // Prevent unknown property injection
    'react/no-unsafe-iframe-src': 'error', // Prevent iframe src injection
    'react/no-unsafe-target-blank': 'error', // Prevent target blank injection
    'react/no-unsafe-unsafe-html': 'error', // Prevent unsafe HTML injection
    'react/no-unsafe-unsafe-jsx': 'error', // Prevent unsafe JSX injection
    'react/no-unsafe-unsafe-jsx-children': 'error', // Prevent unsafe JSX children injection
    'react/no-unsafe-unsafe-jsx-props': 'error', // Prevent unsafe JSX props injection
    'react/no-unsafe-unsafe-jsx-return': 'error', // Prevent unsafe JSX return injection
    'react/no-unsafe-unsafe-jsx-spread': 'error', // Prevent unsafe JSX spread injection
    'react/no-unsafe-unsafe-jsx-template': 'error', // Prevent unsafe JSX template injection
    'react/no-unsafe-unsafe-jsx-text': 'error', // Prevent unsafe JSX text injection
    'react/no-unsafe-unsafe-jsx-value': 'error', // Prevent unsafe JSX value injection
    'react/no-unsafe-unsafe-jsx-whitespace': 'error', // Prevent unsafe JSX whitespace injection
  },
  overrides: [
    {
      files: ['**/*.test.ts', '**/*.test.tsx', '**/*.spec.ts', '**/*.spec.tsx'],
      env: {
        jest: true,
      },
      rules: {
        // Relax some rules for test files
        'no-console': 'off',
        'security/detect-child-process': 'off',
        '@typescript-eslint/no-explicit-any': 'warn',
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
