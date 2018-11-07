import json
import scrapy
import logging
logging.getLogger('scrapy').setLevel(logging.WARNING)

def seqNumAdv(count):
	r = str(count)
	while(len(r) != 6):
		r = '0' + r
	return r

class ospider(scrapy.Spider):

	name = 'spy'
	#start_urls = ['http://oeis.org/A004018/list','http://oeis.org/A000055/list']
	start_urls = ['http://oeis.org/A' + seqNumAdv(i) + '/list' for i in range(999999)]
	
	def parse(self,response):
		raw = response.css('pre::text').extract()
		r1 = raw
		seq = []
		if raw:
			raw[0].replace("\n","")
			i = 0
			while(i < len(raw[0])):
				if raw[0][i] == '\\':
					#current is wrapped
					rawt = raw[0][:i] + raw[0][i+3:]
					raw[0] = rawt
				i += 1
			try:
				seq = json.loads(raw[0])
			except:
				print(response.request.url)
				print(raw[0])
			print(seq,file=open('oeis.txt','a'))
		else:
			print(file=open('oeis.txt','a'))
