/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          50: '#f0f6ff',
          100: '#e0edff',
          500: '#0066ff',
          600: '#0052cc',
          700: '#003d99',
          900: '#002966',
        },
        corporate: {
          dark: '#1e293b',
          gray: '#f8fafc',
          border: '#e2e8f0',
        }
      }
    },
  },
  plugins: [],
}
