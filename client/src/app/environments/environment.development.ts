export const environment = {
  production: false,
  url: 'http://localhost:8080',
  logoutURL : `${import.meta.env.NG_APP_AUTH0_ISSUER}/v2/logout?client_id=${import.meta.env.NG_APP_AUTH0_CLIENT_ID}&returnTo=http://localhost:4200`
};