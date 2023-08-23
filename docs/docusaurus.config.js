// @ts-check
// Note: type annotations allow type checking and IDEs autocompletion

const lightCodeTheme = require('prism-react-renderer/themes/github');
const darkCodeTheme = require('prism-react-renderer/themes/dracula');

/** @type {import('@docusaurus/types').Config} */
const config = {
  title: 'Operator Docs | Glasskube',
  favicon: 'img/favicon.png',
  url: 'https://glasskube.eu/',
  baseUrl: '/docs/',
  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',
  i18n: {defaultLocale: 'en', locales: ['en']},
  presets: [
    [
      'classic',
      /** @type {import('@docusaurus/preset-classic').Options} */
      ({
        docs: {
          routeBasePath: '/',
          sidebarPath: require.resolve('./sidebars.js'),
          editUrl: 'https://github.com/glasskube/operator/tree/main/docs/',
        },
        blog: false,
        pages: false,
        theme: {customCss: require.resolve('./src/css/custom.css')}
      }),
    ],
  ],
  themes: [
    [
      require.resolve("@easyops-cn/docusaurus-search-local"),
      /** @type {import("@easyops-cn/docusaurus-search-local").PluginOptions} */
      ({
        hashed: true,
        indexBlog: false,
        docsRouteBasePath: '/'
      }),
    ]
  ],
  themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
    ({
      image: 'https://cms.glasskube.eu/uploads/Meta_Image_final_0628edc18a.png',
      navbar: {
        title: 'Glasskube Operator',
        logo: {alt: 'Glasskube Logo', src: 'img/glasskube-logo.svg',},
        items: [
          {type: 'docSidebar', sidebarId: 'docsSidebar', label: 'Docs', position: 'left'},
          {href: 'https://github.com/glasskube/operator', label: 'GitHub', position: 'right'}
        ],
      },
      footer: {
        style: 'dark',
        links: [
          {
            title: 'Docs',
            items: [
              {label: 'Getting started', to: 'getting-started/install'},
              {label: 'CRD Reference', to: 'crd-reference'},
            ],
          },
          {
            title: 'More',
            items: [
              {label: 'GitHub', href: 'https://github.com/glasskube/operator'},
            ],
          },
        ],
        copyright: `© ${new Date().getFullYear()} Glasskube OS GmbH.<br>Built with Docusaurus.`,
      },
      prism: {
        theme: lightCodeTheme,
        darkTheme: darkCodeTheme
      },
    }),
};

module.exports = config;
