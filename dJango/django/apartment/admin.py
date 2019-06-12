from django.contrib import admin
from .models import Apart
# Register your models here.
class ApartAdmin(admin.ModelAdmin):
    list_display=("door_password","ho","name","phone","identification",)
    search_fields=['question_text']
admin.site.register(Apart, ApartAdmin)
