FROM nginxinc/nginx-unprivileged:alpine

COPY public /usr/share/nginx/html

EXPOSE  8080

CMD ["nginx", "-g", "daemon off;"]