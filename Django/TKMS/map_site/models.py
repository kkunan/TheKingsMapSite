from django.contrib.gis.db import models
from django.conf import settings
from geoposition.fields import GeopositionField

# Create your models here.


class Event(models.Model):
    event_date = models.DateField('event date')
    event_title_thai = models.CharField(max_length=200)
    event_description_thai = models.TextField()
    event_title_eng = models.CharField(max_length=200)
    event_description_eng = models.TextField()
    event_images = models.ManyToManyField('ImageModel', related_name='event_image_list')

    def __str__(self):
        return self.event_title_thai


class Place(models.Model):
    place_title_thai = models.CharField(max_length=200)
    place_title_eng = models.CharField(max_length=200)
    place_description_thai = models.TextField
    place_description_eng = models.TextField
    place_images = models.ManyToManyField('ImageModel', related_name='place_image_list')
    place_location = GeopositionField()
    place_event_list = models.ManyToManyField('Event', related_name="place_event_list")

    def __str__(self):
        return self.place_title_thai


class ImageModel(models.Model):
    image_name = models.CharField(max_length=200,unique=True)
    image_file = models.ImageField(upload_to=settings.IMAGE_PATH_FIELD_DIRECTORY)

    def __str__(self):
        return self.image_name



