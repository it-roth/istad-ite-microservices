import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'
import { defineConfig } from 'vite'
import path from 'path'
import fs from 'fs'
import { fileURLToPath } from 'url'

const currentDir = path.dirname(fileURLToPath(import.meta.url))

function copyToSpringBuildPlugin() {
  return {
    name: 'copy-to-spring-build',
    closeBundle() {
      const srcDir = path.resolve(currentDir, '../resources/static')
      const targetDirs = [
        path.resolve(currentDir, '../../../build/resources/main/static'),
        path.resolve(currentDir, '../../../../iam-main/build/resources/main/static')
      ]
      for (const targetDir of targetDirs) {
        try {
          if (fs.existsSync(targetDir)) {
            const targetAssets = path.join(targetDir, 'assets')
            if (fs.existsSync(targetAssets)) {
              fs.rmSync(targetAssets, { recursive: true, force: true })
            }
            fs.cpSync(srcDir, targetDir, { recursive: true, force: true })
            console.log(`[copy-to-spring-build] Synced assets to ${targetDir}`)
          }
        } catch (e) {
          console.error(`[copy-to-spring-build] Failed to sync to ${targetDir}:`, e)
        }
      }
    }
  }
}

export default defineConfig({
  base: '/',
  build: {
    outDir: '../resources/static',
    emptyOutDir: true,
  },
  plugins: [react(), tailwindcss(), copyToSpringBuildPlugin()],
  resolve: {
    alias: {
      '@': path.resolve(currentDir, './src'),
    },
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:39000',
        changeOrigin: true,
      },
    },
  },
})
