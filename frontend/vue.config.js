module.exports = {
  publicPath: process.env.VUE_APP_PUBLIC_PATH,
  // Make variables available in SASS for every components
  css: {
    loaderOptions: {
      sass: {
        prependData: `
          @import '@/assets/styles/variables.scss';
        `,
      },
    },
  },
};
