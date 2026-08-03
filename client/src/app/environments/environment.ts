export const environment = {
    production: true,
    url: 'http://api.interviewdocs.net',
    logoutURL : `${import.meta.env.NG_APP_AUTH0_ISSUER}/v2/logout?client_id=${import.meta.env.NG_APP_AUTH0_CLIENT_ID}&returnTo=https://www.interviewdocs.net`
};