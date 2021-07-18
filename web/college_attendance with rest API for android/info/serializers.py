from rest_framework import serializers

from .models import User


#Pinili ko lang yung username and email fields na ipakita for now
class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ('email', 'username')
        