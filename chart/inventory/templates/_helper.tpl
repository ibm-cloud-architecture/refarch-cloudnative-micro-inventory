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
  image: {{ .Values.inventorymysql.image }}:{{ .Values.inventorymysql.imageTag }}
  imagePullPolicy: {{ .Values.inventorymysql.imagePullPolicy }}
  command:
  - "/bin/bash"
  - "-c"
  {{- if .Values.inventorymysql.mysqlPassword }}
  - "until mysql -h ${MYSQL_HOST} -P ${MYSQL_PORT} -u${MYSQL_USER} -p${MYSQL_PASSWORD} -e status; do echo waiting for mysql; sleep 1; done"
  {{- else }}
  - "until mysql -h ${MYSQL_HOST} -P ${MYSQL_PORT} -u${MYSQL_USER} -e status; do echo waiting for mysql; sleep 1; done"
  {{- end }}
  env:
  {{- include "inventory.mysql.environmentvariables" . | indent 2 }}
{{- end }}

{{/* Inventory MySQL Environment Variables */}}
{{- define "inventory.mysql.environmentvariables" }}
- name: MYSQL_HOST
  value: {{ .Values.inventorymysql.fullnameOverride | quote }}
- name: MYSQL_PORT
  value: {{ .Values.inventorymysql.service.port | quote }}
- name: MYSQL_DATABASE
  value: {{ .Values.inventorymysql.mysqlDatabase | quote }}
- name: MYSQL_USER
  value: {{ .Values.inventorymysql.mysqlUser | quote }}
{{- if .Values.inventorymysql.mysqlPassword }}
- name: MYSQL_PASSWORD
  valueFrom:
    secretKeyRef:
      name: {{ .Values.inventorymysql.fullnameOverride | quote }}
      key: mysql-password
{{- end }}
{{- end }}