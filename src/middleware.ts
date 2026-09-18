import { defineMiddleware } from 'astro:middleware';
import { byId, languageUrl } from './utils/courses';

// Starlight generates routes for untranslated default-language pages. Redirect
// them before rendering, so no Bulgarian prose is published or indexed as English.
export const onRequest = defineMiddleware((context, next) => {
  const id = decodeURI(context.url.pathname).replace(/^\/courses\//, '').replace(/\/$/, '');
  if (id.startsWith('en/') && !byId.has(id) && byId.has(id.replace(/^en\//, 'bg/'))) {
    return context.redirect(languageUrl(id, 'en'), 302);
  }
  return next();
});
