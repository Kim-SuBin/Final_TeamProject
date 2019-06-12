from django.db import models

# Create your models here.

class Apart(models.Model):
    door_password=models.IntegerField()
    UUID=models.CharField(max_length=255)
    ho=models.IntegerField()
    name=models.CharField(max_length=30)
    phone=models.CharField(max_length=40)
    identification=models.CharField(max_length=50)

    class Meta:
        verbose_name_plural='apartment'
~
