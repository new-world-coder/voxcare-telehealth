import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react'

interface User {
  id: number
  email: string
  role: string
  name: string
}

interface AuthContextType {
  user: User | null
  isAuthenticated: boolean
  login: (email: string, password: string) => Promise<void>
  logout: () => void
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export const useAuth = () => {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}

interface AuthProviderProps {
  children: ReactNode
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null)
  const [isAuthenticated, setIsAuthenticated] = useState(false)

  useEffect(() => {
    // Check for existing token
    const token = localStorage.getItem('authToken')
    if (token) {
      // Validate token and set user
      setIsAuthenticated(true)
      // For demo purposes, set a mock user
      setUser({
        id: 1,
        email: 'admin@demo.dev',
        role: 'ADMIN',
        name: 'Admin User'
      })
    }
  }, [])

  const login = async (email: string, password: string) => {
    try {
      // Mock login for demo
      if (email === 'admin@demo.dev' && password === 'Passw0rd!') {
        const mockUser = {
          id: 1,
          email: 'admin@demo.dev',
          role: 'ADMIN',
          name: 'Admin User'
        }
        setUser(mockUser)
        setIsAuthenticated(true)
        localStorage.setItem('authToken', 'mock-token')
      } else {
        throw new Error('Invalid credentials')
      }
    } catch (error) {
      throw error
    }
  }

  const logout = () => {
    setUser(null)
    setIsAuthenticated(false)
    localStorage.removeItem('authToken')
  }

  const value: AuthContextType = {
    user,
    isAuthenticated,
    login,
    logout
  }

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  )
}
