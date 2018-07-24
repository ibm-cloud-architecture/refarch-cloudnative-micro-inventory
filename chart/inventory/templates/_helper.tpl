{{- define "inventory.fullname" -}}
  {{- .Release.Name }}-{{ .Chart.Name -}}
{{- end -}}

{{/* MySQL Init Container Template */}}
{{- define "inventory.labels" }}
app: bluecompute
micro: inventory
tier: backend
heritage: {{ .Release.Service | quote }}
release: {{ .Release.Name | quote }}
chart: {{ .Chart.Name }}-{{ .Chart.Version | replace "+" "_" }}
{{- end }}

{{/* MySQL Init Container Template */}}
{{- define "inventory.mysql.initcontainer" }}
- name: test-mysql
  image: {{ .Values.dataloader.image.repository }}:{{ .Values.dataloader.image.tag }}
  imagePullPolicy: {{ .Values.dataloader.image.pullPolicy }}
  {{- if .Values.inventorymysql.enabled }}
  command:
  - "/bin/bash"
  - "-c"
  - "until mysql -h ${MYSQL_HOST} -P ${MYSQL_PORT} -u${MYSQL_USER} -p${MYSQL_PASSWORD} -e status `echo ${uri_path} | cut -d/ -f2`; do echo waiting for mysql; sleep 1; done"
  env:
  - name: MYSQL_HOST
    value: {{ .Values.inventorymysql.fullnameOverride | quote }}
  - name: MYSQL_PORT
    value: {{ .Values.inventorymysql.service.port | quote }}
  - name: MYSQL_USER
    value: {{ .Values.inventorymysql.mysqlUser | quote }}
  - name: MYSQL_PASSWORD
    valueFrom:
      secretKeyRef:
        name: {{ .Values.inventorymysql.fullnameOverride | quote }}
        key: mysql-password
  {{- else if .Values.mysqlURI }}
  command:
  - "/bin/bash"
  - "-c"
  - "source ./helper.sh; uri_parser ${MYSQL_URI}; until mysql -h ${uri_host} -P ${uri_port} -u${uri_user} -p${uri_password} -e status `echo ${uri_path} | cut -d/ -f2`; do echo waiting for mysql; sleep 1; done"
  env:
  - name: MYSQL_URI
    value: {{ .Values.mysqlURI | quote }}
  {{- else }}
  command:
  - "/bin/bash"
  - "-c"
  - "until mysql -h ${MYSQL_HOST} -P ${MYSQL_PORT} -u${MYSQL_USER} -p${MYSQL_PASSWORD} -e status `echo ${uri_path} | cut -d/ -f2`; do echo waiting for mysql; sleep 1; done"
  env:
  - name: MYSQL_HOST
    value: {{ .Values.mysql.host | quote }}
  - name: MYSQL_PORT
    value: {{ .Values.mysql.port | quote }}
  - name: MYSQL_DATABASE
    value: {{ .Values.mysql.database | quote }}
  - name: MYSQL_USER
    value: {{ .Values.mysql.user | quote }}
  - name: MYSQL_PASSWORD
    valueFrom:
      secretKeyRef:
        name: {{ template "inventory.fullname" . }}
        key: mysql-password
  {{- end }}
{{- end }}

{{/* Inventory MySQL Environment Variables */}}
{{- define "inventory.mysql.environmentvariables" }}
{{- if .Values.inventorymysql.enabled }}
- name: MYSQL_HOST
  value: {{ .Values.inventorymysql.fullnameOverride | quote }}
- name: MYSQL_PORT
  value: {{ .Values.inventorymysql.service.port | quote }}
- name: MYSQL_DATABASE
  value: {{ .Values.inventorymysql.mysqlDatabase | quote }}
- name: MYSQL_USER
  value: {{ .Values.inventorymysql.mysqlUser | quote }}
- name: MYSQL_PASSWORD
  valueFrom:
    secretKeyRef:
      name: {{ .Values.inventorymysql.fullnameOverride | quote }}
      key: mysql-password
{{- else if .Values.mysqlURI }}
- name: MYSQL_URI
  value: {{ .Values.mysqlURI | quote }}
{{- else }}
- name: MYSQL_HOST
  value: {{ .Values.mysql.host | quote }}
- name: MYSQL_PORT
  value: {{ .Values.mysql.port | quote }}
- name: MYSQL_DATABASE
  value: {{ .Values.mysql.database | quote }}
- name: MYSQL_USER
  value: {{ .Values.mysql.user | quote }}
- name: MYSQL_PASSWORD
  valueFrom:
    secretKeyRef:
      name: {{ template "inventory.fullname" . }}
      key: mysql-password
{{- end }}
{{- end }}