# Generated by Django 3.1.7 on 2021-05-31 01:20

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('info', '0007_auto_20210531_0744'),
    ]

    operations = [
        migrations.AddField(
            model_name='espdatadaily',
            name='dayStamp',
            field=models.DateField(default='2020-05-10'),
        ),
    ]
