from django.contrib.gis.db import models
from django.contrib.postgres.fields import ArrayField
from django.contrib.gis.geos import Point

# Create your models here.


class Event(models.Model):
    event_date = models.DateField('event date')
    event_title_thai = models.CharField(max_length=200)
    event_description_thai = models.TextField()
    event_title_eng = models.CharField(max_length=200)
    event_description_eng = models.TextField()
    event_images = ArrayField(models.CharField(max_length=300), null=True)


class Place(models.Model):
    place_title_thai = models.CharField(max_length=200)
    place_title_eng = models.CharField(max_length=200)
    place_description_thai = models.TextField
    place_description_eng = models.TextField
    place_images = ArrayField(models.CharField(max_length=300), null=True)
    place_location = models.PointField(unique=True, default=Point(0,0))
    place_event_list = models.ManyToManyField('Event',related_name="place_event_list")
