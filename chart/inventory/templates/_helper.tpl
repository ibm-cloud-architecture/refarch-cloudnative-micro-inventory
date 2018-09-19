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
  image: {{ .Values.mysql.image }}:{{ .Values.mysql.imageTag }}
  imagePullPolicy: {{ .Values.mysql.imagePullPolicy }}
  command:
  - "/bin/bash"
  - "-c"
  {{- if .Values.mysql.mysqlPassword }}
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
  value: {{ .Values.mysql.fullnameOverride | quote }}
- name: MYSQL_PORT
  value: {{ .Values.mysql.service.port | quote }}
- name: MYSQL_DATABASE
  value: {{ .Values.mysql.mysqlDatabase | quote }}
- name: MYSQL_USER
  value: {{ .Values.mysql.mysqlUser | quote }}
{{- if .Values.mysql.mysqlPassword }}
- name: MYSQL_PASSWORD
  valueFrom:
    secretKeyRef:
      name: {{ template "inventory.mysql.secretName" . }}
      key: mysql-password
{{- end }}
{{- end }}

{{/* Inventory MySQL Secret Name */}}
{{- define "inventory.mysql.secretName" }}
  {{- if .Values.mysql.enabled }}
    {{- printf "%s" .Values.mysql.fullnameOverride -}}
  {{- else -}}
    {{ template "inventory.fullname" . }}-mysql-secret
  {{- end }}
{{- end }}