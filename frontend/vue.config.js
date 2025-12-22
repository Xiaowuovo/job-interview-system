module.exports = {
  devServer: {
    port: 8080,
    open: false,
    proxy: {
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        pathRewrite: {
          '^/api': '/api'
        },
        ws: true
      }
    }
  },
  productionSourceMap: false,
  lintOnSave: false
}
