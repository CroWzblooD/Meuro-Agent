FROM node:18-alpine as build
COPY . .
RUN npm install --legacy-peer-deps
RUN npm run build

FROM node:18-alpine as release
COPY --from=build /build ./build
RUN npm install -g serve
EXPOSE 3000
CMD [ "serve", "-s", "build" ]