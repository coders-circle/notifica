# -*- coding: utf-8 -*-
# Generated by Django 1.9 on 2015-12-12 11:33
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('routine', '0002_auto_20151212_1044'),
    ]

    operations = [
        migrations.AlterField(
            model_name='period',
            name='subject',
            field=models.CharField(max_length=100),
        ),
        migrations.AlterField(
            model_name='period',
            name='teachers',
            field=models.CharField(max_length=100),
        ),
    ]
